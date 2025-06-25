package at.wtioit.intellij.plugins.odoo.records.impl;

import at.wtioit.intellij.plugins.odoo.records.OdooRecordService;
import at.wtioit.intellij.plugins.odoo.records.OdooRecord;
import at.wtioit.intellij.plugins.odoo.records.OdooDataUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import java.util.Map;

public class OdooRecordServiceImpl implements OdooRecordService {
    private final Project project;

    public OdooRecordServiceImpl(Project project) {
        this.project = project;
    }

    @Override
    public java.util.stream.Stream<String> getXmlIds() {
        // Return all data record IDs (XML and CSV)
        return OdooDataUtil.findAllDataRecords(project).keySet().stream();
    }

    @Override
    public OdooRecord getRecord(String xmlId) {
        // Return a simple OdooRecord for the given xmlId if found
        OdooDataUtil.DataRecordInfo info = OdooDataUtil.findAllDataRecords(project).get(xmlId);
        if (info != null) {
            return new OdooRecord() {
                @Override
                public String getId() { return info.id; }
                @Override
                public String getXmlId() { return info.id; }
                @Override
                public String getModelName() { return "unknown"; }
                @Override
                public String getPath() { return info.filePath; }
                @Override
                public PsiElement getDefiningElement() { return null; }
                @Override
                public javax.swing.Icon getIcon() { return null; }
                @Override
                public com.intellij.openapi.vfs.VirtualFile findVirtualFile() { return null; }
                @Override
                public at.wtioit.intellij.plugins.odoo.index.OdooIndexSubKeys getSubIndexKey() { return null; }
            };
        }
        return null;
    }

    @Override
    public boolean hasRecord(String xmlId) {
        return OdooDataUtil.findAllDataRecords(project).containsKey(xmlId);
    }

    @Override
    public String ensureFullXmlId(PsiFile file, String refName) {
        // For simplicity, just return refName (could be improved to resolve module prefix)
        return refName;
    }

    @Override
    public boolean hasLocalTemplate(PsiElement element, String id, String xmlId) {
        // Not implemented: always return false (no local template system)
        return false;
    }

    @Override
    public boolean hasGlobalTemplate(String id) {
        // Not implemented: always return false (no global template system)
        return false;
    }
} 