package at.wtioit.intellij.plugins.odoo.records;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlDocument;
import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class OdooDataUtil {
    public static class DataRecordInfo {
        public final String id;
        public final String filePath;
        public final String type; // xml/csv
        public DataRecordInfo(String id, String filePath, String type) {
            this.id = id;
            this.filePath = filePath;
            this.type = type;
        }
    }
    public static Map<String, DataRecordInfo> findAllDataRecords(Project project) {
        Map<String, DataRecordInfo> result = new HashMap<>();
        // XML
        Collection<VirtualFile> xmlFiles = FilenameIndex.getAllFilesByExt(project, "xml", GlobalSearchScope.projectScope(project));
        for (VirtualFile vf : xmlFiles) {
            if (!vf.getPath().contains("/data/")) continue;
            PsiFile psiFile = PsiManager.getInstance(project).findFile(vf);
            if (!(psiFile instanceof XmlFile)) continue;
            XmlFile xmlFile = (XmlFile) psiFile;
            XmlDocument doc = xmlFile.getDocument();
            if (doc == null) continue;
            for (XmlTag tag : doc.getRootTag() != null ? doc.getRootTag().getSubTags() : new XmlTag[0]) {
                walkXmlTag(tag, result, vf.getPath());
            }
        }
        // CSV
        Collection<VirtualFile> csvFiles = FilenameIndex.getAllFilesByExt(project, "csv", GlobalSearchScope.projectScope(project));
        for (VirtualFile vf : csvFiles) {
            if (!vf.getPath().contains("/data/")) continue;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(vf.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                boolean isHeader = true;
                while ((line = reader.readLine()) != null) {
                    if (isHeader) { isHeader = false; continue; }
                    String[] parts = line.split(",");
                    if (parts.length > 0) {
                        String id = parts[0].trim();
                        if (!id.isEmpty()) {
                            result.put(id, new DataRecordInfo(id, vf.getPath(), "csv"));
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
        return result;
    }
    private static void walkXmlTag(XmlTag tag, Map<String, DataRecordInfo> result, String filePath) {
        String tagName = tag.getName();
        if (tagName.equals("record")) {
            String id = tag.getAttributeValue("id");
            if (id != null) {
                result.put(id, new DataRecordInfo(id, filePath, "xml"));
            }
        }
        for (XmlTag child : tag.getSubTags()) {
            walkXmlTag(child, result, filePath);
        }
    }
    public static DataRecordInfo findDataRecordById(Project project, String id) {
        return findAllDataRecords(project).get(id);
    }
    public static String generateDataRecordTemplate(String id, String model) {
        return "<record id=\"" + id + "\" model=\"" + model + "\">\n    <!-- fields -->\n</record>\n";
    }
} 