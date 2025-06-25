package at.wtioit.intellij.plugins.odoo.inspections;

import at.wtioit.intellij.plugins.odoo.OdooBundle;
import at.wtioit.intellij.plugins.odoo.OdooRecordPsiElementMatcherUtil;
import at.wtioit.intellij.plugins.odoo.index.OdooJsModuleFileIndex;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.javascript.psi.JSStringLiteralExpression;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;

public class MissingJsModuleInspection extends LocalInspectionTool {

    @Override
    public @NotNull String getGroupDisplayName() {
        return OdooBundle.message("INSP.GROUP.odoo");
    }

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (OdooRecordPsiElementMatcherUtil.isOdooJsPsiElement(element) && element.getParent() instanceof JSStringLiteralExpression) {
                    JSStringLiteralExpression jsString = (JSStringLiteralExpression) element.getParent();
                    String moduleName = jsString.getStringValue();
                    if (moduleName != null && !moduleName.isEmpty()) {
                        Project project = element.getProject();
                        boolean found = !FileBasedIndex.getInstance().getValues(OdooJsModuleFileIndex.NAME, moduleName, project.getAllScope()).isEmpty();
                        if (!found) {
                            holder.registerProblem(element, OdooBundle.message("INSP.NAME.missing.js.module", moduleName));
                        }
                    }
                }
            }
        };
    }
} 