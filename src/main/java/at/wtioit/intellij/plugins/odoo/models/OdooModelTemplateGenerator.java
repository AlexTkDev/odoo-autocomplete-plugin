package at.wtioit.intellij.plugins.odoo.models;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFileFactory;
import com.jetbrains.python.PythonFileType;

public class OdooModelTemplateGenerator implements IntentionAction {
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "Generate Odoo model template";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        // Only available in Python files
        return file.getFileType().getName().equalsIgnoreCase("Python");
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) {
        // Implemented: Odoo model template generation
        String template = "class MyModel(models.Model):\n    _name = 'my.model'\n    _description = 'My Model'\n    name = fields.Char(string='Name')\n";
        WriteCommandAction.runWriteCommandAction(project, () -> {
            int offset = editor.getCaretModel().getOffset();
            editor.getDocument().insertString(offset, template);
            PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
        });
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
} 