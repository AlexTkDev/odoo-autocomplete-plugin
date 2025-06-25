package at.wtioit.intellij.plugins.odoo.index;

import com.intellij.util.indexing.FileContent;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

// Not supported in Community Edition
public abstract class OdooIndexExtension<T extends OdooIndexEntry> {

    public abstract <E extends OdooIndexEntry> T castValue(E entry);

    public void save(@NotNull DataOutput out, OdooIndexEntry value) throws IOException {
        // getValueExternalizer().save(out, castValue(value));
    }

    public T read(@NotNull DataInput in) throws IOException {
        // return getValueExternalizer().read(in);
        return null;
    }

    public Map<String, OdooIndexEntry> map(FileContent inputData) {
        return Collections.emptyMap();
    }
}
