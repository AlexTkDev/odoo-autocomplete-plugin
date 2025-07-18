package at.wtioit.intellij.plugins.odoo.records.impl;

import at.wtioit.intellij.plugins.odoo.index.OdooIndexSubKeys;
import at.wtioit.intellij.plugins.odoo.models.OdooModel;
import at.wtioit.intellij.plugins.odoo.modules.OdooModule;
import at.wtioit.intellij.plugins.odoo.records.OdooRecord;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class OdooModuleRecord implements OdooRecord {
    private final OdooModule module;

    public OdooModuleRecord(OdooModule module) {
        this.module = module;
    }

    @Override
    public OdooIndexSubKeys getSubIndexKey() {
        return OdooIndexSubKeys.ODOO_MODULES;
    }

    @Override
    public @NotNull String getId() {
        return "module_" + module.getName();
    }

    @Override
    public @Nullable String getXmlId() {
        return "base." + getId();
    }

    @Override
    public @NotNull String getPath() {
        return null;
    }

    @Override
    public @NotNull String getModelName() {
        return "ir.module.module";
    }

    @Override
    public PsiElement getDefiningElement() {
        return null;
    }

    @Override
    public @Nullable VirtualFile findVirtualFile() {
        return null;
    }

    @Override
    public @Nullable Icon getIcon() {
        return module.getIcon();
    }
}
