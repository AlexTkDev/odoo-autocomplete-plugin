package at.wtioit.intellij.plugins.odoo.inspections;

import at.wtioit.intellij.plugins.odoo.OdooBundle;
import at.wtioit.intellij.plugins.odoo.OdooRecordPsiElementMatcherUtil;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.xml.XmlToken;
import com.jetbrains.python.psi.PyStringElement;
import org.jetbrains.annotations.NotNull;

public class MissingPoMsgIdInspection extends LocalInspectionTool {

    @Override
    public @NotNull String getGroupDisplayName() {
        return OdooBundle.message("INSP.GROUP.odoo");
    }

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (OdooRecordPsiElementMatcherUtil.isOdooPoPsiElement(element)) {
                    String msgid = null;
                    if (element instanceof PyStringElement) {
                        msgid = ((PyStringElement) element).toString();
                    } else if (element instanceof XmlToken) {
                        msgid = element.getText();
                    }

                    if (msgid != null && !msgid.isEmpty()) {
                        Project project = element.getProject();
                        // [PyCharm Community: Index API removed]
                    }
                }
            }
        };
    }
} 