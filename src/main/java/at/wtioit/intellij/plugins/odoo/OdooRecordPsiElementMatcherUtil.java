package at.wtioit.intellij.plugins.odoo;

import at.wtioit.intellij.plugins.odoo.records.OdooRecord;
import at.wtioit.intellij.plugins.odoo.records.index.OdooRecordImpl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPlainText;
import com.intellij.psi.xml.*;
import com.jetbrains.python.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static at.wtioit.intellij.plugins.odoo.PsiElementsUtil.*;
import static com.intellij.psi.xml.XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN;

public interface OdooRecordPsiElementMatcherUtil {

    List<String> ODOO_RECORD_REF_ATTRIBUTES = Collections.unmodifiableList(Arrays.asList(
            "ref",
            "inherit_id",
            "groups", // TODO: Handle comma separated groups
            "action",
            "t-call",
            "t-call-assets",
            "t-extend",
            "view_id",
            "res_model",
            "src_model",
            "menu",
            "parent_menu_id",
            "binding_model_id"
    ));

    Map<String, List<String>> ODOO_RECORD_REF_ATTRIBUTES_TAGS_FALSE_POSITIVES = Map.of(
            // TODO: Tree and kanban actions need to be available as methods in the model
            "action", Collections.unmodifiableList(Arrays.asList("form", "tree", "kanban", "widget", "div"))
    );

    List<String> ODOO_XML_RECORD_TYPES = Arrays.asList("record", "template", "menuitem", "act_window", "report");
    String NULL_XML_ID_KEY = ":UNDETECTED_XML_ID:";


    static boolean isOdooRecordPsiElement(PsiElement psiElement) {
        if (psiElement instanceof XmlToken && ((XmlToken) psiElement).getTokenType() == XML_ATTRIBUTE_VALUE_TOKEN) {
            XmlAttribute attribute = PsiElementsUtil.findParent(psiElement, XmlAttribute.class, 2);
            if (attribute != null) {
                String attributeName = attribute.getName();
                if (ODOO_RECORD_REF_ATTRIBUTES.contains(attributeName)) {
                    if (ODOO_RECORD_REF_ATTRIBUTES_TAGS_FALSE_POSITIVES.containsKey(attributeName)) {
                        String tagName = attribute.getParent().getName();
                        return !ODOO_RECORD_REF_ATTRIBUTES_TAGS_FALSE_POSITIVES.get(attributeName).contains(tagName);
                    }
                    if ("groups".equals(attributeName) || "groups_id".equals(attributeName)) {
                        String value = attribute.getValue();
                        if (value != null && value.contains(",")) {
                            int offset = psiElement.getTextOffset() - attribute.getValueElement().getTextOffset();
                            String[] ids = value.split(",");
                            int pos = 0;
                            for (String id : ids) {
                                int start = pos;
                                int end = pos + id.length();
                                if (offset >= start && offset <= end) {
                                    return true;
                                }
                                pos = end + 1;
                            }
                        }
                    }
                    return true;
                }
            }

        }
        if (psiElement instanceof PyStringElement) {
            PyArgumentList argumentList = findParent(psiElement, PyArgumentList.class, 2);
            if (argumentList != null) {
                PyReferenceExpression method = getPrevSibling(argumentList, PyReferenceExpression.class);
                if (method != null) {
                    // TODO use type of self.env (Environment) instead of text to check if method belongs to Environment
                    String methodName = method.getLastChild().getText();
                    return "ref".equals(methodName);
                }
            }
        }
        return false;
    }

    static boolean holdsOdooRecordReference(PsiElement psiElement) {
        XmlAttribute attribute = PsiElementsUtil.findParent(psiElement, XmlAttribute.class, 2);
        if (attribute != null && "eval".equals(attribute.getName())) {
            if (psiElement.getText().contains("ref(")) {
                return true;
            }
        }
        return false;
    }

    static String[] csvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        char quoteChar = 0;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == quoteChar) {
                    if (i + 1 < line.length() && line.charAt(i + 1) == quoteChar) {
                        sb.append(quoteChar); // escaped quote
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    sb.append(c);
                }
            } else {
                if (c == '"' || c == '\'') {
                    inQuotes = true;
                    quoteChar = c;
                } else if (c == ',') {
                    result.add(sb.toString().trim());
                    sb.setLength(0);
                } else {
                    sb.append(c);
                }
            }
        }
        result.add(sb.toString().trim());
        return result.toArray(new String[0]);
    }

    static Map<String, OdooRecord> getRecordsFromOdooTag(XmlTag odooTag, @NotNull String path, Function<OdooRecord, Boolean> function, int limit) {
        HashMap<String, OdooRecord> records = new HashMap<>();
        PsiElementsUtil.walkTree(odooTag, (tag)-> {
            String name = tag.getName();
            // data needs further investigation (can hold records / templates)
            if ("data".equals(name)) return PsiElementsUtil.TREE_WALING_SIGNAL.should_skip(records.size() >= limit);
            // function needs no further investigation (cannot hold records / templates)
            if ("function".equals(name)) return PsiElementsUtil.TREE_WALING_SIGNAL.SKIP_CHILDREN;
            if (ODOO_XML_RECORD_TYPES.contains(name)) {
                OdooRecord record = OdooRecordImpl.getFromXml(tag, path);
                if (applyRecord(function, records, record)) return PsiElementsUtil.TREE_WALING_SIGNAL.SKIP_CHILDREN;
            }
            // investigate children if we need more records
            return PsiElementsUtil.TREE_WALING_SIGNAL.investigate(records.size() < limit);
        }, XmlTag.class, 2);
        return records;
    }

    static Map<String, OdooRecord> getRecordsFromTemplateTag(XmlTag recordsTag, @NotNull String path, Function<OdooRecord, Boolean> function, int limit) {
        HashMap<String, OdooRecord> records = new HashMap<>();
        PsiElementsUtil.walkTree(recordsTag, (tag)-> {
            String name = tag.getAttributeValue("t-name");
            if (name != null) {
                OdooRecord record = OdooRecordImpl.getFromXmlTemplate(tag, path);
                if (applyRecord(function, records, record)) return PsiElementsUtil.TREE_WALING_SIGNAL.SKIP_CHILDREN;
            }
            // investigate children if we need more records
            return PsiElementsUtil.TREE_WALING_SIGNAL.investigate(records.size() < limit);
        }, XmlTag.class, 2);
        return records;
    }

    static OdooRecord getAssetBundleRecord(@NotNull String xmlId, String path, @NotNull PsiElement psiElement) {
        // TODO it seems that those assets bundles have no xmlId in the odoo database, so the model name is not the correct one
        return OdooRecordImpl.getFromData(xmlId, xmlId, "ir.asset_bundle", path, psiElement);
    }

    static boolean applyRecord(Function<OdooRecord, Boolean> function, HashMap<String, OdooRecord> records, OdooRecord record) {
        if (record == null) {
            return true;
        }
        if (function.apply(record)) {
            if (record.getXmlId() == null) {
                records.put(NULL_XML_ID_KEY + "." + record.getId(), record);
            } else {
                records.put(record.getXmlId(), record);
            }
            return true;
        }
        return false;
    }

    static boolean isOdooViewPsiElement(PsiElement psiElement) {
        if (psiElement instanceof XmlToken && ((XmlToken) psiElement).getTokenType() == XML_ATTRIBUTE_VALUE_TOKEN) {
            XmlAttribute attribute = PsiElementsUtil.findParent(psiElement, XmlAttribute.class, 2);
            if (attribute != null && "view_id".equals(attribute.getName())) {
                return true;
            }
        }
        return false;
    }

    static boolean isOdooActionPsiElement(PsiElement psiElement) {
        if (psiElement instanceof XmlToken && ((XmlToken) psiElement).getTokenType() == XML_ATTRIBUTE_VALUE_TOKEN) {
            XmlAttribute attribute = PsiElementsUtil.findParent(psiElement, XmlAttribute.class, 2);
            if (attribute != null && "action".equals(attribute.getName())) {
                return true;
            }
        }
        return false;
    }

    static boolean isOdooPoPsiElement(PsiElement psiElement) {
        // For Python, check if it's a string inside _() or gettext()
        if (psiElement.getLanguage().isKindOf("Python")) {
            PyArgumentList argumentList = findParent(psiElement, PyArgumentList.class, 2);
            if (argumentList != null) {
                PyExpression callee = ((PyCallExpression) argumentList.getParent()).getCallee();
                if (callee != null) {
                    String calleeName = callee.getText();
                    return "_".equals(calleeName) || "gettext".equals(calleeName);
                }
            }
        }
        // For XML, check for attributes that contain translatable strings
        if (psiElement.getLanguage().isKindOf("XML")) {
             if (psiElement instanceof XmlToken && ((XmlToken) psiElement).getTokenType() == XML_ATTRIBUTE_VALUE_TOKEN) {
                XmlAttribute attribute = findParent(psiElement, XmlAttribute.class, 2);
                return attribute != null && "string".equals(attribute.getName());
            }
        }
        return false;
    }

}
