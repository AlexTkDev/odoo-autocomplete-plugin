package at.wtioit.intellij.plugins.odoo.records;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class OdooCsvRecordUtil {
    public static class CsvRecordInfo {
        public final String id;
        public final String[] row;
        public final String filePath;
        public CsvRecordInfo(String id, String[] row, String filePath) {
            this.id = id;
            this.row = row;
            this.filePath = filePath;
        }
    }

    public static Map<String, CsvRecordInfo> findAllCsvRecords(Project project) {
        Map<String, CsvRecordInfo> result = new HashMap<>();
        Collection<VirtualFile> csvFiles = FilenameIndex.getAllFilesByExt(project, "csv", GlobalSearchScope.projectScope(project));
        for (VirtualFile vf : csvFiles) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(vf.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                boolean isHeader = true;
                while ((line = reader.readLine()) != null) {
                    if (isHeader) { isHeader = false; continue; }
                    String[] parts = line.split(",");
                    if (parts.length > 0) {
                        String id = parts[0].trim();
                        if (!id.isEmpty()) {
                            result.put(id, new CsvRecordInfo(id, parts, vf.getPath()));
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
        return result;
    }

    public static CsvRecordInfo findCsvRecordById(Project project, String id) {
        return findAllCsvRecords(project).get(id);
    }

    public static String generateCsvRecordTemplate(String id, String... columns) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", columns)).append("\n");
        sb.append(id);
        for (int i = 1; i < columns.length; i++) sb.append(",");
        sb.append("\n");
        return sb.toString();
    }
} 