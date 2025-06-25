package at.wtioit.intellij.plugins.odoo.index;

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
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OdooPoMsgIdFileIndex extends OdooIndexExtension<OdooPoMsgIdIE> {
    @NonNls public static final ID<String, OdooPoMsgIdIE> NAME = ID.create("OdooPoMsgIdFileIndex");
    OdooPoMsgIdFileIndexer indexer = new OdooPoMsgIdFileIndexer();

    @Override
    public @NotNull ID<String, OdooPoMsgIdIE> getName() { return NAME; }
    @Override
    public @NotNull DataIndexer<String, OdooPoMsgIdIE, FileContent> getIndexer() { return indexer; }
    @Override
    public @NotNull KeyDescriptor<String> getKeyDescriptor() { return EnumeratorStringDescriptor.INSTANCE; }
    @Override
    public @NotNull DataExternalizer<OdooPoMsgIdIE> getValueExternalizer() {
        return new DataExternalizer<OdooPoMsgIdIE>() {
            @Override
            public void save(@NotNull DataOutput out, OdooPoMsgIdIE value) throws IOException {
                out.writeUTF(value.getMsgid());
                out.writeUTF(value.getFilePath());
                out.writeUTF(value.getFileName());
            }
            @Override
            public OdooPoMsgIdIE read(@NotNull DataInput in) throws IOException {
                String msgid = in.readUTF();
                String filePath = in.readUTF();
                String fileName = in.readUTF();
                return new OdooPoMsgIdIE(msgid, filePath, fileName);
            }
        };
    }
    @Override
    public int getVersion() { return 1; }
    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return file -> {
            String ext = file.getFileType().getDefaultExtension();
            return ext.equals("po") || ext.equals("pot");
        };
    }
    @Override
    public boolean dependsOnFileContent() { return true; }
    private static class OdooPoMsgIdFileIndexer extends OdooDataIndexer<OdooPoMsgIdIE> {
        private static final Pattern MSGID_PATTERN = Pattern.compile("msgid \\\"(.*?)\\\"");
        @Override
        public @NotNull Map<String, OdooPoMsgIdIE> mapWatched(@NotNull FileContent inputData) {
            String text = inputData.getContentAsText().toString();
            Matcher matcher = MSGID_PATTERN.matcher(text);
            Map<String, OdooPoMsgIdIE> result = new HashMap<>();
            while (matcher.find()) {
                String msgid = matcher.group(1);
                result.put(msgid, new OdooPoMsgIdIE(msgid, inputData.getFile().getPath(), inputData.getFile().getName()));
            }
            return result;
        }
    }
    @Override
    public <E extends OdooIndexEntry> OdooPoMsgIdIE castValue(E entry) {
        if (entry instanceof OdooPoMsgIdIE) return (OdooPoMsgIdIE) entry;
        throw new IllegalArgumentException("expected OdooPoMsgIdIE");
    }
} 