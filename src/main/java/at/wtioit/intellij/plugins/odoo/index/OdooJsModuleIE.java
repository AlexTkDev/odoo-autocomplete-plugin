package at.wtioit.intellij.plugins.odoo.index;

import java.util.Objects;

public class OdooJsModuleIE implements OdooIndexEntry {
    private final String moduleName;
    private final String filePath;
    private final String fileName;

    public OdooJsModuleIE(String moduleName, String filePath, String fileName) {
        this.moduleName = moduleName;
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public String getModuleName() { return moduleName; }
    public String getFilePath() { return filePath; }
    public String getFileName() { return fileName; }

    @Override
    public OdooIndexSubKeys getSubIndexKey() {
        return OdooIndexSubKeys.ODOO_JS_MODULES;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OdooJsModuleIE that = (OdooJsModuleIE) o;
        return Objects.equals(moduleName, that.moduleName) &&
               Objects.equals(filePath, that.filePath) &&
               Objects.equals(fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleName, filePath, fileName);
    }
} 