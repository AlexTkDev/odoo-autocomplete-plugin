package at.wtioit.intellij.plugins.odoo.index;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.FileContent;
import com.intellij.util.indexing.ID;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OdooJsModuleFileIndex extends OdooIndexExtension<OdooJsModuleIE> {
    @NonNls public static final ID<String, OdooJsModuleIE> NAME = ID.create("OdooJsModuleFileIndex");
    OdooJsModuleFileIndexer indexer = new OdooJsModuleFileIndexer();

    @Override
    public @NotNull ID<String, OdooJsModuleIE> getName() { return NAME; }
    @Override
    public @NotNull DataIndexer<String, OdooJsModuleIE, FileContent> getIndexer() { return indexer; }
    @Override
    public @NotNull KeyDescriptor<String> getKeyDescriptor() { return EnumeratorStringDescriptor.INSTANCE; }
    @Override
    public @NotNull DataExternalizer<OdooJsModuleIE> getValueExternalizer() {
        return new DataExternalizer<OdooJsModuleIE>() {
            @Override
            public void save(@NotNull DataOutput out, OdooJsModuleIE value) throws IOException {
                out.writeUTF(value.getModuleName());
                out.writeUTF(value.getFilePath());
                out.writeUTF(value.getFileName());
            }
            @Override
            public OdooJsModuleIE read(@NotNull DataInput in) throws IOException {
                String moduleName = in.readUTF();
                String filePath = in.readUTF();
                String fileName = in.readUTF();
                return new OdooJsModuleIE(moduleName, filePath, fileName);
            }
        };
    }
    @Override
    public int getVersion() { return 1; }
    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return file -> file.getFileType().getDefaultExtension().equals("js");
    }
    @Override
    public boolean dependsOnFileContent() { return true; }
    private static class OdooJsModuleFileIndexer extends OdooDataIndexer<OdooJsModuleIE> {
        private static final Pattern ODOO_DEFINE_PATTERN = Pattern.compile("odoo\\.define\\(['\"]([a-zA-Z0-9_.]+)['\"]");
        @Override
        public @NotNull Map<String, OdooJsModuleIE> mapWatched(@NotNull FileContent inputData) {
            String text = inputData.getContentAsText().toString();
            Matcher matcher = ODOO_DEFINE_PATTERN.matcher(text);
            Map<String, OdooJsModuleIE> result = new HashMap<>();
            while (matcher.find()) {
                String moduleName = matcher.group(1);
                result.put(moduleName, new OdooJsModuleIE(moduleName, inputData.getFile().getPath(), inputData.getFile().getName()));
            }
            return result;
        }
    }
    @Override
    public <E extends OdooIndexEntry> OdooJsModuleIE castValue(E entry) {
        if (entry instanceof OdooJsModuleIE) return (OdooJsModuleIE) entry;
        throw new IllegalArgumentException("expected OdooJsModuleIE");
    }
} 