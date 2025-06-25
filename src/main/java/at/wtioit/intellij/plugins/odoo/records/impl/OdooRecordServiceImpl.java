package at.wtioit.intellij.plugins.odoo.records.impl;

import at.wtioit.intellij.plugins.odoo.records.OdooRecordService;
import at.wtioit.intellij.plugins.odoo.records.OdooIndex;
import at.wtioit.intellij.plugins.odoo.records.OdooIndexSubKeys;
import org.jetbrains.annotations.NotNull;

public class OdooRecordServiceImpl implements OdooRecordService {

    @Override
    public java.util.stream.Stream<String> getXmlIds() {
        return OdooIndex.getAllKeys(OdooIndexSubKeys.ODOO_RECORDS, project);
    }
} 