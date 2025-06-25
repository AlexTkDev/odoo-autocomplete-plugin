package at.wtioit.intellij.plugins.odoo.index;

/**
 * Base interface for Odoo index entries (models, records, modules, JS modules, PO msgids, etc.).
 */
public interface OdooIndexEntry {
    OdooIndexSubKeys getSubIndexKey();
}
