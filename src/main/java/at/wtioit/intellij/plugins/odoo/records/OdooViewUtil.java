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

public class OdooViewUtil {
    public static class OdooViewInfo {
        public final String id;
        public final String model;
        public final PsiElement definingElement;
        public final String filePath;
        public OdooViewInfo(String id, String model, PsiElement definingElement, String filePath) {
            this.id = id;
            this.model = model;
            this.definingElement = definingElement;
            this.filePath = filePath;
        }
    }

    public static Map<String, OdooViewInfo> findAllViews(Project project) {
        Map<String, OdooViewInfo> result = new HashMap<>();
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

    private static void walkTag(XmlTag tag, Map<String, OdooViewInfo> result, String filePath) {
        String tagName = tag.getName();
        if (tagName.equals("record")) {
            String id = tag.getAttributeValue("id");
            String model = tag.getAttributeValue("model");
            if ("ir.ui.view".equals(model) && id != null) {
                result.put(id, new OdooViewInfo(id, model, tag, filePath));
            }
        }
        for (XmlTag child : tag.getSubTags()) {
            walkTag(child, result, filePath);
        }
    }

    public static OdooViewInfo findViewById(Project project, String id) {
        return findAllViews(project).get(id);
    }

    public static String generateViewTemplate(String id, String model) {
        return "<record id=\"" + id + "\" model=\"ir.ui.view\">\n    <field name=\"name\">" + id + "</field>\n    <field name=\"model\">" + model + "</field>\n    <field name=\"arch\" type=\"xml\">\n        <form string=\"Form\">\n            <!-- fields -->\n        </form>\n    </field>\n</record>\n";
    }
} 