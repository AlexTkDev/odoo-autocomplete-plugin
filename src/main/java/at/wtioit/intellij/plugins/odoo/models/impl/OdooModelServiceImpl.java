package at.wtioit.intellij.plugins.odoo.models.impl;

import at.wtioit.intellij.plugins.odoo.index.OdooIndex;
import at.wtioit.intellij.plugins.odoo.index.OdooIndexSubKeys;
import at.wtioit.intellij.plugins.odoo.models.OdooModel;
import at.wtioit.intellij.plugins.odoo.models.OdooModelService;
import at.wtioit.intellij.plugins.odoo.models.OdooModelUtil;
import at.wtioit.intellij.plugins.odoo.models.index.OdooModelIE;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.stream.StreamSupport;

public class OdooModelServiceImpl implements OdooModelService {

    private final Project project;

    public OdooModelServiceImpl(Project project) {
        this.project = project;
    }

    @Override
    public boolean hasModel(String name) {
        if (OdooIndex.getAllKeys(OdooIndexSubKeys.ODOO_MODELS, project).anyMatch(k -> k.equals(name))) {
            return true;
        }
        return matchedByWildcardName(name) != null;
    }

    @Nullable
    private String matchedByWildcardName(@Nullable  String name) {
        if (name == null) return null;
        return OdooIndex.getAllKeys(OdooIndexSubKeys.ODOO_MODELS, project)
                .filter(modelName -> OdooModelUtil.wildcardNameMatches(modelName, name))
                .findFirst()
                .orElse(null);
    }
}
