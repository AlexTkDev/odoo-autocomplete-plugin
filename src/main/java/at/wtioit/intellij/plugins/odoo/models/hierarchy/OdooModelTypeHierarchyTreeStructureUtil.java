package at.wtioit.intellij.plugins.odoo.models.hierarchy;

import at.wtioit.intellij.plugins.odoo.WithinProject;
import at.wtioit.intellij.plugins.odoo.models.OdooModel;
import at.wtioit.intellij.plugins.odoo.models.OdooModelService;
import at.wtioit.intellij.plugins.odoo.models.impl.OdooModelImpl;
import at.wtioit.intellij.plugins.odoo.modules.OdooModule;
import at.wtioit.intellij.plugins.odoo.modules.OdooModuleService;
import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class OdooModelTypeHierarchyTreeStructureUtil {

    static void addOdooModelChildren(@NotNull OdooModel model, @NotNull HierarchyNodeDescriptor hierarchyNodeDescriptor, @NotNull List<Object> children) {
        if (hierarchyNodeDescriptor instanceof OdooModelHierarchyNodeDescriptor) {
            // skip listing the same classes over and over again
            return;
        }
        Project project = hierarchyNodeDescriptor.getProject();
        if (model instanceof OdooModelImpl) {
            // ... existing code ...
        } else {
            WithinProject.run(project, () -> {
                // ... existing code ...
            });
        }
    }

    private static int comparePaths(PsiElement e1, PsiElement e2) {
        // TODO: Prefer models in the 'models' directory?
        return e1.getContainingFile().getVirtualFile().getPath().compareTo(e2.getContainingFile().getVirtualFile().getPath());
    }

    public static Object[] buildChildren(@NotNull PsiElement psiElement, @NotNull HierarchyNodeDescriptor hierarchyNodeDescriptor, @NotNull List<Object> children) {
        children = new ArrayList<>(children);
        Project project = hierarchyNodeDescriptor.getProject();
        if (project != null) {
            // ... existing code ...
        }
        return children.toArray();
    }
}
