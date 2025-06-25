package at.wtioit.intellij.plugins.odoo.modules;

import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.stream.Stream;

public interface OdooModuleService {

    Iterable<OdooModule> getModules();

    @Nullable
    OdooModule getModule(String moduleName);

    // Not supported in Community Edition

    OdooModule findModule(String moduleName);

    @Nullable
    PsiDirectory getOdooDirectory();

    @Nullable
    PsiDirectory getOdooModuleDirectory(String path);

    static boolean isValidOdooModuleDirectory(@Nullable String path) {
        return path != null
                // /setup/.. modules are just a copy/symlink of the ones not in /setup/ (OCA)
                && !path.contains(File.separator + "setup" + File.separator)
                // when using remote debugging (e.g. with docker) pycharm may have remote sources that duplicate our modules
                && !path.contains(File.separator + "remote_sources" + File.separator);
    }

    Stream<String> getModuleNames();
}
