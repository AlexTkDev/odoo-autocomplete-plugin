package at.wtioit.intellij.plugins.odoo.records;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

public interface OdooRecordService {
    // TODO: Consider using Iterable for better memory consumption if the number of records is large
    java.util.stream.Stream<String> getXmlIds();

    OdooRecord getRecord(String xmlId);

    boolean hasRecord(String xmlId);

    String ensureFullXmlId(PsiFile file, String refName);

    boolean hasLocalTemplate(PsiElement element, String id, String xmlId);

    boolean hasGlobalTemplate(String id);
}
