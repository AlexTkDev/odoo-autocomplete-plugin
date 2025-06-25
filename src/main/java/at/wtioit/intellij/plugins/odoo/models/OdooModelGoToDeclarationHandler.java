package at.wtioit.intellij.plugins.odoo.models;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import com.jetbrains.python.psi.PyReferenceExpression;
import com.jetbrains.python.psi.PyClass;
import at.wtioit.intellij.plugins.odoo.models.OdooModelUtil;
import at.wtioit.intellij.plugins.odoo.records.*;
import com.intellij.psi.xml.XmlToken;
import com.intellij.openapi.actionSystem.DataContext;

public class OdooModelGoToDeclarationHandler implements GotoDeclarationHandler {
    @Nullable
    @Override
    public PsiElement[] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        // Models (PyStringLiteralExpression)
        if (sourceElement instanceof PyStringLiteralExpression) {
            String modelName = ((PyStringLiteralExpression) sourceElement).getStringValue();
            java.util.Map<String, com.intellij.psi.PsiElement> models = OdooModelUtil.findAllOdooModels(sourceElement.getProject());
            if (models.containsKey(modelName)) {
                return new PsiElement[] { models.get(modelName) };
            }
        }
        // Fields (PyReferenceExpression)
        if (sourceElement instanceof PyReferenceExpression) {
            PyReferenceExpression ref = (PyReferenceExpression) sourceElement;
            PsiElement qualifier = ref.getQualifier();
            if (qualifier instanceof PyReferenceExpression && "self".equals(((PyReferenceExpression) qualifier).getReferencedName())) {
                String fieldName = ref.getReferencedName();
                PyClass pyClass = com.intellij.psi.util.PsiTreeUtil.getParentOfType(sourceElement, PyClass.class);
                if (pyClass != null && fieldName != null) {
                    OdooModelUtil.OdooFieldInfo field = OdooModelUtil.getFieldInfo(pyClass, fieldName);
                    if (field != null && field.definingElement != null) {
                        return new PsiElement[] { field.definingElement };
                    }
                }
            }
        }
        // XML id navigation (XmlToken)
        if (sourceElement instanceof XmlToken) {
            String id = sourceElement.getText();
            // Actions
            OdooActionUtil.OdooActionInfo action = OdooActionUtil.findActionById(sourceElement.getProject(), id);
            if (action != null && action.definingElement != null) return new PsiElement[] { action.definingElement };
            // Views
            OdooViewUtil.OdooViewInfo view = OdooViewUtil.findViewById(sourceElement.getProject(), id);
            if (view != null && view.definingElement != null) return new PsiElement[] { view.definingElement };
            // Menus
            OdooMenuUtil.OdooMenuInfo menu = OdooMenuUtil.findMenuById(sourceElement.getProject(), id);
            if (menu != null && menu.definingElement != null) return new PsiElement[] { menu.definingElement };
            // Groups
            OdooGroupUtil.OdooGroupInfo group = OdooGroupUtil.findGroupById(sourceElement.getProject(), id);
            if (group != null && group.definingElement != null) return new PsiElement[] { group.definingElement };
            // Reports
            OdooReportUtil.OdooReportInfo report = OdooReportUtil.findReportById(sourceElement.getProject(), id);
            if (report != null && report.definingElement != null) return new PsiElement[] { report.definingElement };
            // Data
            OdooDataUtil.DataRecordInfo data = OdooDataUtil.findDataRecordById(sourceElement.getProject(), id);
            // For data you can return null if there is no definingElement
            // Security (groups)
            OdooSecurityUtil.SecurityGroupInfo secGroup = OdooSecurityUtil.findSecurityGroupById(sourceElement.getProject(), id);
            if (secGroup != null) {
                // You can find XML element via OdooGroupUtil
                OdooGroupUtil.OdooGroupInfo g = OdooGroupUtil.findGroupById(sourceElement.getProject(), id);
                if (g != null && g.definingElement != null) return new PsiElement[] { g.definingElement };
            }
            // Access rules (CSV)
            OdooSecurityUtil.AccessRuleInfo access = OdooSecurityUtil.findAccessRuleById(sourceElement.getProject(), id);
            // For access you can return null if there is no definingElement
            // CSV records
            OdooCsvRecordUtil.CsvRecordInfo csv = OdooCsvRecordUtil.findCsvRecordById(sourceElement.getProject(), id);
            // For csv you can return null if there is no definingElement
        }
        return PsiElement.EMPTY_ARRAY;
    }

    @Nullable
    @Override
    public String getActionText(DataContext context) {
        return null;
    }
} 