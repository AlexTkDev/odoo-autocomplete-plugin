package at.wtioit.intellij.plugins.odoo.modules;

import at.wtioit.intellij.plugins.odoo.index.OdooIndexEntry;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.List;

public interface OdooModule extends OdooIndexEntry {

    @NotNull
    String getName();

    @Nullable
    PsiElement getDirectory();

    @NotNull
    String getPath();

    @Nullable
    Icon getIcon();

    @Nullable
    String getRelativeLocationString();

    @NotNull
    Iterable<OdooModule> getDependencies();

    // Not supported in Community Edition

    boolean dependsOn(@NotNull OdooModule module);

    @Nullable
    NavigatablePsiElement getNavigationItem();
}
