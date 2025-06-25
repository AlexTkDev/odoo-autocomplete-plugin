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
import com.intellij.ide.highlighter.XmlFileType;
import java.util.Map;
import java.util.HashMap;

public class OdooActionUtil {
    public static class OdooActionInfo {
        public final String id;
        public final String model;
        public final PsiElement definingElement;
        public final String filePath;
        public OdooActionInfo(String id, String model, PsiElement definingElement, String filePath) {
            this.id = id;
            this.model = model;
            this.definingElement = definingElement;
            this.filePath = filePath;
        }
    }

    public static Map<String, OdooActionInfo> findAllActions(Project project) {
        Map<String, OdooActionInfo> result = new HashMap<>();
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

    private static void walkTag(XmlTag tag, Map<String, OdooActionInfo> result, String filePath) {
        String tagName = tag.getName();
        if (tagName.equals("record")) {
            String id = tag.getAttributeValue("id");
            String model = tag.getAttributeValue("model");
            if (model != null && model.startsWith("ir.actions.")) {
                if (id != null) {
                    result.put(id, new OdooActionInfo(id, model, tag, filePath));
                }
            }
        }
        for (XmlTag child : tag.getSubTags()) {
            walkTag(child, result, filePath);
        }
    }

    public static OdooActionInfo findActionById(Project project, String id) {
        return findAllActions(project).get(id);
    }

    public static String generateActionTemplate(String id, String model) {
        return "<record id=\"" + id + "\" model=\"" + model + "\">\n    <!-- action fields -->\n</record>\n";
    }
} 