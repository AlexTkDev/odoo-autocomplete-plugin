package at.wtioit.intellij.plugins.odoo;

import com.intellij.openapi.project.Project;

import java.util.function.Supplier;

// TODO: Remove this workaround once everything is migrated to indexes
public class WithinProject extends ThreadLocal<Project> {
    public static WithinProject INSTANCE = new WithinProject();

    public static <V> V call(Project project, Supplier<V> supplier) {
        if (INSTANCE.get() != null) return supplier.get();
        try {
            INSTANCE.set(project);
            return supplier.get();
        } finally {
            INSTANCE.remove();
        }
    }

    public static void run(Project project, Runnable runnable) {
        if (INSTANCE.get() != null) runnable.run();
        try {
            INSTANCE.set(project);
            runnable.run();
        } finally {
            INSTANCE.remove();
        }
    }
}
