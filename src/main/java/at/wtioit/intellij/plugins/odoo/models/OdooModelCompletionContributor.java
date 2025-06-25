package at.wtioit.intellij.plugins.odoo.models;

import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import org.jetbrains.annotations.NotNull;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import at.wtioit.intellij.plugins.odoo.models.OdooModelUtil;

public class OdooModelCompletionContributor extends CompletionContributor {
    public OdooModelCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withParent(PyStringLiteralExpression.class),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        // Implemented: Odoo model autocompletion
                        java.util.Map<String, com.intellij.psi.PsiElement> models = OdooModelUtil.findAllOdooModels(parameters.getPosition().getProject());
                        for (String model : models.keySet()) {
                            result.addElement(LookupElementBuilder.create(model));
                        }
                    }
                }
        );
    }
} 