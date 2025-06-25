package at.wtioit.intellij.plugins.odoo.models;

import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import at.wtioit.intellij.plugins.odoo.models.OdooModelUtil;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyClass;

public class OdooXmlModelCompletionContributor extends CompletionContributor {
    public OdooXmlModelCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withParent(XmlAttributeValue.class),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        // Implemented: Odoo model autocompletion for XML
                        java.util.Map<String, com.intellij.psi.PsiElement> models = OdooModelUtil.findAllOdooModels(parameters.getPosition().getProject());
                        for (String model : models.keySet()) {
                            result.addElement(LookupElementBuilder.create(model));
                        }

                        // Autocompletion for <field name="..."> in XML
                        PsiElement position = parameters.getPosition();
                        XmlTag xmlTag = com.intellij.psi.util.PsiTreeUtil.getParentOfType(position, XmlTag.class);
                        if (xmlTag != null && "field".equals(xmlTag.getName())) {
                            XmlTag modelTag = xmlTag.getParentTag();
                            if (modelTag != null) {
                                String modelName = modelTag.getAttributeValue("model");
                                if (modelName != null) {
                                    if (models.containsKey(modelName) && models.get(modelName) instanceof PyClass) {
                                        PyClass pyClass = (PyClass) models.get(modelName);
                                        for (OdooModelUtil.OdooFieldInfo field : OdooModelUtil.findOdooFieldsInModel(pyClass)) {
                                            result.addElement(LookupElementBuilder.create(field.name));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
        );
    }
} 