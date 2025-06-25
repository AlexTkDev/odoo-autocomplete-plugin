package at.wtioit.intellij.plugins.odoo.models;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.openapi.editor.Document;
import com.jetbrains.python.psi.PyClass;
import com.intellij.psi.PsiElement;
import at.wtioit.intellij.plugins.odoo.records.*;
import com.intellij.psi.xml.XmlToken;

public class OdooModelQuickFix implements LocalQuickFix {
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getName() {
        return "Create Odoo model";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return getName();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiElement element = descriptor.getPsiElement();
        PsiFile file = element.getContainingFile();
        Document document = PsiDocumentManager.getInstance(project).getDocument(file);
        if (document != null) {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                int offset = element.getTextOffset();
                String template = null;
                // Determine the type and generate the template
                if (element instanceof XmlToken) {
                    String id = element.getText();
                    // Actions
                    if (OdooActionUtil.findActionById(project, id) == null) {
                        template = OdooActionUtil.generateActionTemplate(id, "ir.actions.server");
                    } else if (OdooViewUtil.findViewById(project, id) == null) {
                        template = OdooViewUtil.generateViewTemplate(id, "model.name");
                    } else if (OdooMenuUtil.findMenuById(project, id) == null) {
                        template = OdooMenuUtil.generateMenuTemplate(id, id);
                    } else if (OdooGroupUtil.findGroupById(project, id) == null) {
                        template = OdooGroupUtil.generateGroupTemplate(id, id);
                    } else if (OdooReportUtil.findReportById(project, id) == null) {
                        template = OdooReportUtil.generateReportTemplate(id, id, "model.name");
                    } else if (OdooDataUtil.findDataRecordById(project, id) == null) {
                        template = OdooDataUtil.generateDataRecordTemplate(id, "model.name");
                    } else if (OdooSecurityUtil.findSecurityGroupById(project, id) == null) {
                        template = OdooSecurityUtil.generateAccessRuleTemplate(id, "model", "group", "1", "1", "1", "1");
                    } else if (OdooCsvRecordUtil.findCsvRecordById(project, id) == null) {
                        template = OdooCsvRecordUtil.generateCsvRecordTemplate(id, "id", "name", "model_id:id", "group_id:id", "perm_read", "perm_write", "perm_create", "perm_unlink");
                    }
                }
                if (template != null) {
                    document.insertString(offset, template);
                    PsiDocumentManager.getInstance(project).commitDocument(document);
                    return;
                }
                // By default — insert model template
                template = "\nclass MyModel(models.Model):\n    _name = 'my.model'\n    _description = 'My Model'\n    name = fields.Char(string='Name')\n";
                document.insertString(offset, template);
                PsiDocumentManager.getInstance(project).commitDocument(document);
            });
        }
    }
} 