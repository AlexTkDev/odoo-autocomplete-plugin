package at.wtioit.intellij.plugins.odoo;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionUtilCore;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyFromImportStatement;
import com.jetbrains.python.psi.PyStringElement;
import com.jetbrains.python.psi.PyReferenceExpression;
import com.jetbrains.python.psi.PyClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import at.wtioit.intellij.plugins.odoo.models.OdooModelUtil;

import static at.wtioit.intellij.plugins.odoo.PsiElementsUtil.findParent;

public class OdooCompletionContributor {

    private final Logger logger = Logger.getInstance(OdooCompletionContributor.class);

    @Nullable
    private String getStringValue(@NotNull CompletionParameters parameters, String expressionWithDummy) {
        PsiElement position = parameters.getPosition();
        if (position instanceof PyStringElement) {
            TextRange contentRange = ((PyStringElement) position).getContentRange();
            String content = expressionWithDummy.substring(contentRange.getStartOffset(), contentRange.getEndOffset());
            return content.replace(CompletionUtilCore.DUMMY_IDENTIFIER, "")
                    .replace(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED, "");
        }
        return null;
    }

    void suggestPoMsgId(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result, String value) {
        com.intellij.openapi.project.Project project = parameters.getOriginalFile().getProject();
        java.util.List<String> allMsgIds = new java.util.ArrayList<>();
        // index.processAllKeys(at.wtioit.intellij.plugins.odoo.index.OdooPoMsgIdFileIndex.NAME, msgid -> {
        //     allMsgIds.add(msgid);
        //     return true;
        // }, project);
        for (String msgid : allMsgIds) {
            if (msgid.startsWith(value)) {
                result.addElement(com.intellij.codeInsight.lookup.LookupElementBuilder.create(msgid));
            }
        }
    }

    public void suggestOdooField(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        PsiElement position = parameters.getPosition();
        if (position.getParent() instanceof PyReferenceExpression) {
            PyReferenceExpression ref = (PyReferenceExpression) position.getParent();
            PsiElement qualifier = ref.getQualifier();
            if (qualifier instanceof PyReferenceExpression && "self".equals(((PyReferenceExpression) qualifier).getReferencedName())) {
                PyClass pyClass = PsiElementsUtil.findParent(position, PyClass.class);
                if (pyClass != null) {
                    for (OdooModelUtil.OdooFieldInfo field : OdooModelUtil.findOdooFieldsInModel(pyClass)) {
                        result.addElement(LookupElementBuilder.create(field.name));
                    }
                }
            }
        }
    }
}
