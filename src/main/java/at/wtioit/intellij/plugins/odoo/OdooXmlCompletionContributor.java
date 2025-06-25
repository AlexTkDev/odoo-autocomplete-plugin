package at.wtioit.intellij.plugins.odoo;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionUtilCore;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import com.intellij.codeInsight.lookup.LookupElementBuilder;

public class OdooXmlCompletionContributor extends AbstractOdooCompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        if (OdooModelPsiElementMatcherUtil.isOdooModelPsiElement(parameters.getPosition())) {
            String value = parameters.getPosition().getText()
                    .replace(CompletionUtilCore.DUMMY_IDENTIFIER, "")
                    .replace(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED, "");
            suggestModelName(parameters, result, value);
        }
        if (OdooRecordPsiElementMatcherUtil.isOdooRecordPsiElement(parameters.getPosition())) {
            String value = parameters.getPosition().getText()
                    .replace(CompletionUtilCore.DUMMY_IDENTIFIER, "")
                    .replace(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED, "");
            suggestRecordXmlId(parameters, result, value);
        }
        if (OdooRecordPsiElementMatcherUtil.isOdooViewPsiElement(parameters.getPosition())) {
            String value = parameters.getPosition().getText()
                    .replace(CompletionUtilCore.DUMMY_IDENTIFIER, "")
                    .replace(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED, "");
            suggestViewXmlId(parameters, result, value);
        }
        if (OdooRecordPsiElementMatcherUtil.isOdooActionPsiElement(parameters.getPosition())) {
            String value = parameters.getPosition().getText()
                    .replace(CompletionUtilCore.DUMMY_IDENTIFIER, "")
                    .replace(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED, "");
            suggestActionXmlId(parameters, result, value);
        }
        if (OdooRecordPsiElementMatcherUtil.isOdooMenuPsiElement(parameters.getPosition())) {
            String value = parameters.getPosition().getText()
                    .replace(CompletionUtilCore.DUMMY_IDENTIFIER, "")
                    .replace(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED, "");
            suggestMenuXmlId(parameters, result, value);
        }
        if (OdooRecordPsiElementMatcherUtil.isOdooReportPsiElement(parameters.getPosition())) {
            String value = parameters.getPosition().getText()
                    .replace(CompletionUtilCore.DUMMY_IDENTIFIER, "")
                    .replace(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED, "");
            suggestReportXmlId(parameters, result, value);
        }
        if (OdooRecordPsiElementMatcherUtil.isOdooGroupPsiElement(parameters.getPosition())) {
            String value = parameters.getPosition().getText()
                    .replace(CompletionUtilCore.DUMMY_IDENTIFIER, "")
                    .replace(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED, "");
            suggestGroupXmlId(parameters, result, value);
        }
        if (OdooRecordPsiElementMatcherUtil.isOdooCsvPsiElement(parameters.getPosition())) {
            String value = parameters.getPosition().getText();
            suggestCsvXmlId(parameters, result, value);
        }
        if (OdooRecordPsiElementMatcherUtil.isOdooJsPsiElement(parameters.getPosition())) {
            String value = parameters.getPosition().getText();
            suggestJsModuleName(parameters, result, value);
        }
        if (OdooRecordPsiElementMatcherUtil.isOdooPoPsiElement(parameters.getPosition())) {
            String value = parameters.getPosition().getText();
            suggestPoXmlId(parameters, result, value);
        }
    }

    private void suggestViewXmlId(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result, String value) {
        suggestRecordXmlIdFiltered(parameters, result, value, "ir.ui.view");
    }

    private void suggestActionXmlId(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result, String value) {
        suggestRecordXmlIdFiltered(parameters, result, value, "ir.actions.");
    }

    private void suggestRecordXmlIdFiltered(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result, String value, String modelPrefix) {
        PsiFile file = parameters.getOriginalFile();
        Map<String, at.wtioit.intellij.plugins.odoo.records.OdooRecord> records = OdooRecordPsiElementMatcherUtil.getRecordsFromFile(file);
        for (Map.Entry<String, at.wtioit.intellij.plugins.odoo.records.OdooRecord> entry : records.entrySet()) {
            at.wtioit.intellij.plugins.odoo.records.OdooRecord record = entry.getValue();
            if (record.getModel() != null && record.getModel().startsWith(modelPrefix)) {
                result.addElement(createLookupElement(entry.getKey(), record));
            }
        }
    }

    private void suggestMenuXmlId(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result, String value) {
        suggestRecordXmlIdFiltered(parameters, result, value, "ir.ui.menu");
    }

    private void suggestReportXmlId(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result, String value) {
        suggestRecordXmlIdFiltered(parameters, result, value, "ir.actions.report");
    }

    private void suggestGroupXmlId(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result, String value) {
        suggestRecordXmlIdFiltered(parameters, result, value, "res.groups");
    }

    private void suggestCsvXmlId(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result, String value) {
        PsiFile file = parameters.getOriginalFile();
        Map<String, at.wtioit.intellij.plugins.odoo.records.OdooRecord> records = OdooRecordPsiElementMatcherUtil.getRecordsFromFile(file);
        for (Map.Entry<String, at.wtioit.intellij.plugins.odoo.records.OdooRecord> entry : records.entrySet()) {
            at.wtioit.intellij.plugins.odoo.records.OdooRecord record = entry.getValue();
            result.addElement(
                LookupElementBuilder.create(entry.getKey())
                    .withIcon(record.getIcon())
                    .withTailText(" " + record.getPath(), true)
            );
        }
    }

    private void suggestJsModuleName(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result, String value) {
        result.addElement(createSimpleLookupElement("my_module"));
    }

    private void suggestPoXmlId(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result, String value) {
        result.addElement(createSimpleLookupElement("my_xml_id"));
    }

    private LookupElementBuilder createLookupElement(String key, at.wtioit.intellij.plugins.odoo.records.OdooRecord record) {
        return LookupElementBuilder.create(key)
                .withIcon(record.getIcon())
                .withTailText(" " + record.getPath(), true);
    }

    private LookupElementBuilder createSimpleLookupElement(String key) {
        return LookupElementBuilder.create(key);
    }
}
