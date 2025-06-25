package at.wtioit.intellij.plugins.odoo.models.impl;

import at.wtioit.intellij.plugins.odoo.index.OdooIndexSubKeys;
import at.wtioit.intellij.plugins.odoo.models.OdooModel;
import at.wtioit.intellij.plugins.odoo.modules.OdooModule;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

public class OdooModelImpl implements OdooModel {
    private final String name;
    private final Project project;
    private OdooModule baseModule = null;

    public OdooModelImpl(String modelName, Project project) {
        this.name = modelName;
        this.project = project;
    }

    @Override
    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public OdooModule getBaseModule() {
        return baseModule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OdooModelImpl that = (OdooModelImpl) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public OdooIndexSubKeys getSubIndexKey() {
        return OdooIndexSubKeys.ODOO_MODELS;
    }

    @Override
    public Set<OdooModule> getModules() {
        return java.util.Collections.emptySet();
    }

    @Override
    public com.intellij.psi.PsiElement getDefiningElement() {
        return null;
    }
}
