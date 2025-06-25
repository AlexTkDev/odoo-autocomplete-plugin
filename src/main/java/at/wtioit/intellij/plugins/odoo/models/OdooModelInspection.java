package at.wtioit.intellij.plugins.odoo.models;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ProblemHighlightType;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyReferenceExpression;
import com.jetbrains.python.psi.PyClass;
import at.wtioit.intellij.plugins.odoo.OdooModelPsiElementMatcherUtil;
import at.wtioit.intellij.plugins.odoo.models.OdooModelUtil;
import org.jetbrains.annotations.NotNull;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlAttribute;
import at.wtioit.intellij.plugins.odoo.records.*;
import com.intellij.psi.xml.XmlToken;
import com.intellij.psi.PsiElement;

public class OdooModelInspection extends LocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new PyElementVisitor() {
            @Override
            public void visitPyStringLiteralExpression(PyStringLiteralExpression element) {
                super.visitPyStringLiteralExpression(element);
                if (OdooModelPsiElementMatcherUtil.isOdooModelPsiElement(element)) {
                    String modelName = element.getStringValue();
                    if (!OdooModelUtil.findAllOdooModels(element.getProject()).containsKey(modelName)) {
                        holder.registerProblem(element, "Odoo model '" + modelName + "' not found", ProblemHighlightType.ERROR);
                    }
                }
            }
            @Override
            public void visitPyReferenceExpression(PyReferenceExpression element) {
                super.visitPyReferenceExpression(element);
                PsiElement qualifier = element.getQualifier();
                if (qualifier instanceof PyReferenceExpression && "self".equals(((PyReferenceExpression) qualifier).getReferencedName())) {
                    String fieldName = element.getReferencedName();
                    PyClass pyClass = com.intellij.psi.util.PsiTreeUtil.getParentOfType(element, PyClass.class);
                    if (pyClass != null && fieldName != null && !OdooModelUtil.hasField(pyClass, fieldName)) {
                        holder.registerProblem(element, "Odoo field '" + fieldName + "' not found in model", ProblemHighlightType.ERROR);
                    }
                }
            }
            @Override
            public void visitElement(com.intellij.psi.PsiElement element) {
                super.visitElement(element);
                // Inspection for <field name="..."> in XML
                if (element instanceof XmlAttribute) {
                    XmlAttribute attr = (XmlAttribute) element;
                    if ("name".equals(attr.getName())) {
                        XmlTag fieldTag = attr.getParent();
                        if (fieldTag != null && "field".equals(fieldTag.getName())) {
                            XmlTag modelTag = fieldTag.getParentTag();
                            if (modelTag != null) {
                                String modelName = modelTag.getAttributeValue("model");
                                String fieldName = attr.getValue();
                                if (modelName != null && fieldName != null) {
                                    java.util.Map<String, com.intellij.psi.PsiElement> models = OdooModelUtil.findAllOdooModels(element.getProject());
                                    if (models.containsKey(modelName) && models.get(modelName) instanceof PyClass) {
                                        PyClass pyClass = (PyClass) models.get(modelName);
                                        if (!OdooModelUtil.hasField(pyClass, fieldName)) {
                                            holder.registerProblem(attr, "Odoo field '" + fieldName + "' not found in model '" + modelName + "'", ProblemHighlightType.ERROR);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // Inspection for XML id (XmlToken)
                if (element instanceof XmlToken) {
                    String id = element.getText();
                    if (OdooActionUtil.findActionById(element.getProject(), id) != null) return;
                    if (OdooViewUtil.findViewById(element.getProject(), id) != null) return;
                    if (OdooMenuUtil.findMenuById(element.getProject(), id) != null) return;
                    if (OdooGroupUtil.findGroupById(element.getProject(), id) != null) return;
                    if (OdooReportUtil.findReportById(element.getProject(), id) != null) return;
                    if (OdooDataUtil.findDataRecordById(element.getProject(), id) != null) return;
                    if (OdooSecurityUtil.findSecurityGroupById(element.getProject(), id) != null) return;
                    if (OdooSecurityUtil.findAccessRuleById(element.getProject(), id) != null) return;
                    if (OdooCsvRecordUtil.findCsvRecordById(element.getProject(), id) != null) return;
                    // If not found in any utility — error
                    holder.registerProblem(element, "Odoo id '" + id + "' not found in project", ProblemHighlightType.ERROR);
                }
            }
        };
    }
} 