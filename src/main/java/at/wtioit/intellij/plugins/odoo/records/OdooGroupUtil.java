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

public class OdooGroupUtil {
    public static class OdooGroupInfo {
        public final String id;
        public final PsiElement definingElement;
        public final String filePath;
        public OdooGroupInfo(String id, PsiElement definingElement, String filePath) {
            this.id = id;
            this.definingElement = definingElement;
            this.filePath = filePath;
        }
    }

    public static Map<String, OdooGroupInfo> findAllGroups(Project project) {
        Map<String, OdooGroupInfo> result = new HashMap<>();
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

    private static void walkTag(XmlTag tag, Map<String, OdooGroupInfo> result, String filePath) {
        String tagName = tag.getName();
        if (tagName.equals("record")) {
            String id = tag.getAttributeValue("id");
            String model = tag.getAttributeValue("model");
            if ("res.groups".equals(model) && id != null) {
                result.put(id, new OdooGroupInfo(id, tag, filePath));
            }
        }
        for (XmlTag child : tag.getSubTags()) {
            walkTag(child, result, filePath);
        }
    }

    public static OdooGroupInfo findGroupById(Project project, String id) {
        return findAllGroups(project).get(id);
    }

    public static String generateGroupTemplate(String id, String name) {
        return "<record id=\"" + id + "\" model=\"res.groups\">\n    <field name=\"name\">" + name + "</field>\n    <!-- category_id, implied_ids, ... -->\n</record>\n";
    }
} 