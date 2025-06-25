package at.wtioit.intellij.plugins.odoo.index;

import java.util.Objects;

public class OdooPoMsgIdIE implements OdooIndexEntry {
    private final String msgid;
    private final String filePath;
    private final String fileName;

    public OdooPoMsgIdIE(String msgid, String filePath, String fileName) {
        this.msgid = msgid;
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public String getMsgid() { return msgid; }
    public String getFilePath() { return filePath; }
    public String getFileName() { return fileName; }

    @Override
    public OdooIndexSubKeys getSubIndexKey() {
        return OdooIndexSubKeys.ODOO_PO_MSGIDS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OdooPoMsgIdIE that = (OdooPoMsgIdIE) o;
        return Objects.equals(msgid, that.msgid) &&
               Objects.equals(filePath, that.filePath) &&
               Objects.equals(fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(msgid, filePath, fileName);
    }
} 