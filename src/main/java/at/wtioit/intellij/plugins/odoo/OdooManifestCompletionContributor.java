package at.wtioit.intellij.plugins.odoo;

import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import org.jetbrains.annotations.NotNull;
import com.intellij.codeInsight.lookup.LookupElementBuilder;

public class OdooManifestCompletionContributor extends CompletionContributor {
    private static final String[] MANIFEST_KEYS = new String[] {
        "name", "version", "summary", "description", "author", "website", "category", "depends", "data", "demo", "installable", "application", "auto_install"
    };
    public OdooManifestCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withParent(PyStringLiteralExpression.class),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        for (String key : MANIFEST_KEYS) {
                            result.addElement(LookupElementBuilder.create(key));
                        }
                    }
                }
        );
    }
} 