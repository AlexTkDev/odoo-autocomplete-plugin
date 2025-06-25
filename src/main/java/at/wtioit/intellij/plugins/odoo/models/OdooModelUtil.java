package at.wtioit.intellij.plugins.odoo.models;

import at.wtioit.intellij.plugins.odoo.PsiElementsUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.jetbrains.python.psi.resolve.PyResolveContext;
import com.jetbrains.python.psi.types.TypeEvalContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.python.PythonFileType;
import com.jetbrains.python.psi.PyAssignmentStatement;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyReferenceExpression;
import com.jetbrains.python.psi.PyKeywordArgument;
import com.jetbrains.python.psi.PyArgumentList;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class OdooModelUtil {

    private static final Logger logger = Logger.getInstance(OdooModelUtil.class);
    public static final String NAME_WILDCARD_MARKER = ":ANYTHING:";
    private static final Map<String, Pattern> regexCache = new ConcurrentHashMap<>();

    public static String detectName(PsiElement pyline) {
        return detectName(pyline, () -> PyResolveContext.defaultContext().withTypeEvalContext(TypeEvalContext.codeAnalysis(pyline.getContainingFile().getProject(), pyline.getContainingFile())));
    }

    public static String detectName(PsiElement pyline, Supplier<PyResolveContext> contextSupplier) {
        String name = null;
        for (PsiElement statement : pyline.getChildren()[1].getChildren()) {
            PsiElement statementFirstChild = statement.getFirstChild();
            if (statementFirstChild != null) {
                String variableName = statementFirstChild.getText();
                if (OdooModel.ODOO_MODEL_NAME_VARIABLE_NAME.contains(variableName)) {
                    PsiElement valueChild = statement.getLastChild();
                    while (valueChild instanceof PsiComment || valueChild instanceof PsiWhiteSpace) {
                        valueChild = valueChild.getPrevSibling();
                    }
                    String stringValueForChild = PsiElementsUtil.getStringValueForValueChild(valueChild, contextSupplier);
                    if (stringValueForChild != null) {
                        name = stringValueForChild;
                    }
                    // if we get the name from _name it takes precedence over _inherit
                    if ("_name".equals(variableName)) break;
                }
            }
        }
        if (name == null) logger.debug("Cannot detect name for " + pyline + " in " + pyline.getContainingFile());
        return name;
    }

    public static boolean wildcardNameMatches(String nameWithWildcard, String name) {
        if (!nameWithWildcard.contains(NAME_WILDCARD_MARKER))  {
            return false;
        }
        if (!regexCache.containsKey(nameWithWildcard)) {
            String rePattern = nameWithWildcard.replaceAll("\\.", "\\\\.").replace(NAME_WILDCARD_MARKER, ".*");
            regexCache.put(nameWithWildcard, Pattern.compile(rePattern));
        }
        Pattern pattern = regexCache.get(nameWithWildcard);
        return pattern.matcher(name).matches();
    }

    public static String removeWildcards(String modelName) {
        return modelName.replaceAll(NAME_WILDCARD_MARKER, "");
    }

    // Information about Odoo field
    public static class OdooFieldInfo {
        public final String name;
        public final String type;
        public final String comodelName; // for Many2one, Many2many, One2many
        public final PsiElement definingElement;

        public OdooFieldInfo(String name, String type, String comodelName, PsiElement definingElement) {
            this.name = name;
            this.type = type;
            this.comodelName = comodelName;
            this.definingElement = definingElement;
        }
    }

    /**
     * Find all Odoo models in the project
     */
    public static Map<String, PsiElement> findAllOdooModels(Project project) {
        Map<String, PsiElement> result = new HashMap<>();
        Collection<VirtualFile> pyFiles = FileTypeIndex.getFiles(PythonFileType.INSTANCE, GlobalSearchScope.projectScope(project));
        PsiManager psiManager = PsiManager.getInstance(project);
        for (VirtualFile vf : pyFiles) {
            PsiFile psiFile = psiManager.findFile(vf);
            if (psiFile == null) continue;
            for (PsiElement element : psiFile.getChildren()) {
                if (element instanceof PyClass) {
                    PyClass pyClass = (PyClass) element;
                    if (at.wtioit.intellij.plugins.odoo.OdooModelPsiElementMatcherUtil.isOdooModelDefinition(pyClass)) {
                        String modelName = detectName(pyClass);
                        if (modelName != null && !modelName.isEmpty()) {
                            result.put(modelName, pyClass);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Find all Odoo fields in the model class
     */
    public static List<OdooFieldInfo> findOdooFieldsInModel(PyClass pyClass) {
        List<OdooFieldInfo> fields = new ArrayList<>();
        for (PsiElement element : pyClass.getStatementList().getStatements()) {
            if (element instanceof PyAssignmentStatement) {
                PyAssignmentStatement assignment = (PyAssignmentStatement) element;
                PyExpression[] targets = assignment.getTargets();
                if (targets.length == 0) continue;
                String fieldName = targets[0].getText();
                PyExpression assignedValue = assignment.getAssignedValue();
                if (assignedValue instanceof PyCallExpression) {
                    PyCallExpression call = (PyCallExpression) assignedValue;
                    PyExpression callee = call.getCallee();
                    if (callee != null && callee instanceof PyReferenceExpression) {
                        String type = callee.getText();
                        if (type.startsWith("fields.")) {
                            String comodelName = null;
                            // For Many2one, Many2many, One2many search comodel_name
                            if (type.equals("fields.Many2one") || type.equals("fields.Many2many") || type.equals("fields.One2many")) {
                                PyArgumentList argList = call.getArgumentList();
                                if (argList != null) {
                                    // First positional argument
                                    PyExpression[] args = argList.getArguments();
                                    if (args.length > 0 && args[0] instanceof PyReferenceExpression == false) {
                                        comodelName = PsiElementsUtil.getStringValueForValueChild(args[0]);
                                    }
                                    // Or named argument comodel_name
                                    for (PyExpression arg : args) {
                                        if (arg instanceof PyKeywordArgument) {
                                            PyKeywordArgument kw = (PyKeywordArgument) arg;
                                            if ("comodel_name".equals(kw.getKeyword())) {
                                                comodelName = kw.getValueExpression().getText();
                                            }
                                        }
                                    }
                                }
                            }
                            fields.add(new OdooFieldInfo(fieldName, type, comodelName, assignment));
                        }
                    }
                }
            }
        }
        return fields;
    }

    /**
     * Check if the model contains a field with the given name
     */
    public static boolean hasField(PyClass pyClass, String fieldName) {
        for (OdooFieldInfo field : findOdooFieldsInModel(pyClass)) {
            if (field.name.equals(fieldName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get field info by name
     */
    public static OdooFieldInfo getFieldInfo(PyClass pyClass, String fieldName) {
        for (OdooFieldInfo field : findOdooFieldsInModel(pyClass)) {
            if (field.name.equals(fieldName)) {
                return field;
            }
        }
        return null;
    }
}
