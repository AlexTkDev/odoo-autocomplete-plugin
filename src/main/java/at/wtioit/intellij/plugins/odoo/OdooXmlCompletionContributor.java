package at.wtioit.intellij.plugins.odoo;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionUtilCore;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import at.wtioit.intellij.plugins.odoo.records.*;

public class OdooXmlCompletionContributor {

    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        if (OdooModelPsiElementMatcherUtil.isOdooModelPsiElement(parameters.getPosition())) {
            // Models (example)
            for (String model : at.wtioit.intellij.plugins.odoo.models.OdooModelUtil.findAllOdooModels(parameters.getPosition().getProject()).keySet()) {
                result.addElement(LookupElementBuilder.create(model));
            }
        }
        if (OdooRecordPsiElementMatcherUtil.isOdooRecordPsiElement(parameters.getPosition())) {
            // Data records (XML and CSV)
            for (OdooDataUtil.DataRecordInfo info : OdooDataUtil.findAllDataRecords(parameters.getPosition().getProject()).values()) {
                result.addElement(LookupElementBuilder.create(info.id).withTailText(" "+info.filePath, true));
            }
        }
        if (OdooRecordPsiElementMatcherUtil.isOdooViewPsiElement(parameters.getPosition())) {
            // Views
            for (OdooViewUtil.OdooViewInfo info : OdooViewUtil.findAllViews(parameters.getPosition().getProject()).values()) {
                result.addElement(LookupElementBuilder.create(info.id).withTailText(" "+info.filePath, true));
            }
        }
        if (OdooRecordPsiElementMatcherUtil.isOdooActionPsiElement(parameters.getPosition())) {
            // Actions
            for (OdooActionUtil.OdooActionInfo info : OdooActionUtil.findAllActions(parameters.getPosition().getProject()).values()) {
                result.addElement(LookupElementBuilder.create(info.id).withTailText(" "+info.filePath, true));
            }
        }
        // Removed unsupported isOdooMenuPsiElement, isOdooGroupPsiElement, isOdooReportPsiElement, isOdooSecurityGroupPsiElement, isOdooAccessRulePsiElement, isOdooCsvRecordPsiElement
    }

    private void suggestViewXmlId(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result, String value) {
        suggestRecordXmlIdFiltered(parameters, result, value, "ir.ui.view");
    }

    private void suggestActionXmlId(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result, String value) {
        suggestRecordXmlIdFiltered(parameters, result, value, "ir.actions.");
    }

    private void suggestRecordXmlIdFiltered(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result, String value, String modelPrefix) {
        Map<String, at.wtioit.intellij.plugins.odoo.records.OdooRecord> records = new java.util.HashMap<>();
        for (Map.Entry<String, at.wtioit.intellij.plugins.odoo.records.OdooRecord> entry : records.entrySet()) {
            at.wtioit.intellij.plugins.odoo.records.OdooRecord record = entry.getValue();
            result.addElement(createLookupElement(entry.getKey(), record));
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
        Map<String, at.wtioit.intellij.plugins.odoo.records.OdooRecord> records = new java.util.HashMap<>();
        for (Map.Entry<String, at.wtioit.intellij.plugins.odoo.records.OdooRecord> entry : records.entrySet()) {
            at.wtioit.intellij.plugins.odoo.records.OdooRecord record = entry.getValue();
            result.addElement(
                LookupElementBuilder.create(entry.getKey())
                    .withIcon(record.getIcon())
                    .withTailText(" " + record.getPath(), true)
            );
        }
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
