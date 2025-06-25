package at.wtioit.intellij.plugins.odoo.records;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.ide.highlighter.XmlFileType;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class OdooRecordUtil {
    public static class OdooRecordInfo {
        public final String id;
        public final String model;
        public final PsiElement definingElement;
        public final String filePath;
        public OdooRecordInfo(String id, String model, PsiElement definingElement, String filePath) {
            this.id = id;
            this.model = model;
            this.definingElement = definingElement;
            this.filePath = filePath;
        }
    }

    /**
     * Find all records (records) Odoo in the project (by xml_id)
     */
    public static Map<String, OdooRecordInfo> findAllRecords(Project project) {
        Map<String, OdooRecordInfo> result = new HashMap<>();
        for (VirtualFile vf : FileTypeIndex.getFiles(XmlFileType.INSTANCE, GlobalSearchScope.projectScope(project))) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(vf);
            if (!(psiFile instanceof XmlFile)) continue;
            XmlFile xmlFile = (XmlFile) psiFile;
            XmlDocument doc = xmlFile.getDocument();
            if (doc == null) continue;
            for (XmlTag tag : doc.getRootTag() != null ? doc.getRootTag().getSubTags() : new XmlTag[0]) {
                walkTag(tag, result, vf.getPath());
            }
        }
        return result;
    }

    private static void walkTag(XmlTag tag, Map<String, OdooRecordInfo> result, String filePath) {
        String tagName = tag.getName();
        if (tagName.equals("record") || tagName.equals("template") || tagName.equals("menuitem") || tagName.equals("act_window") || tagName.equals("report")) {
            String id = tag.getAttributeValue("id");
            String model = tag.getAttributeValue("model");
            if (model == null) {
                // templates: template, menuitem, act_window, report
                if (tagName.equals("template")) model = "ir.ui.view";
                else if (tagName.equals("menuitem")) model = "ir.ui.menu";
                else if (tagName.equals("report")) model = "ir.actions.report";
                else if (tagName.equals("act_window")) model = "ir.actions.act_window";
            }
            if (id != null && model != null) {
                result.put(id, new OdooRecordInfo(id, model, tag, filePath));
            }
        }
        for (XmlTag child : tag.getSubTags()) {
            walkTag(child, result, filePath);
        }
    }

    /**
     * Find a record by xml_id
     */
    public static OdooRecordInfo findRecordById(Project project, String id) {
        return findAllRecords(project).get(id);
    }

    /**
     * Generate record template for quick fix
     */
    public static String generateRecordTemplate(String id, String model) {
        return "<record id=\"" + id + "\" model=\"" + model + "\">\n    <!-- fields -->\n</record>\n";
    }
} 