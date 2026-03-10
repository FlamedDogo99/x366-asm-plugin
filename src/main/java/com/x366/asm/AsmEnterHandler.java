package com.x366.asm;

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class AsmEnterHandler implements EnterHandlerDelegate {

    @Override
    public Result preprocessEnter(@NotNull PsiFile file, @NotNull Editor editor, @NotNull Ref<Integer> caretOffset, @NotNull Ref<Integer> caretAdvance, @NotNull DataContext dataContext, EditorActionHandler originalHandler) {

        if(!(file.getFileType() instanceof AsmFileType)) {
            return Result.Continue;
        }

        Document doc = editor.getDocument();
        int offset = caretOffset.get();
        int lineNumber = doc.getLineNumber(offset);
        int lineStart = doc.getLineStartOffset(lineNumber);
        CharSequence lineText = doc.getImmutableCharSequence().subSequence(lineStart, offset);

        // check for standalone comment
        String trimmed = lineText.toString().stripLeading();
        if(trimmed.startsWith(";")) {
            String leading = lineText.subSequence(0, lineText.length() - trimmed.length()).toString();
            String insertion = "\n" + leading + "; ";
            doc.insertString(offset, insertion);
            editor.getCaretModel().moveToOffset(offset + insertion.length());
            return Result.Stop;
        }

        return Result.Continue;
    }

    @Override
    public Result postProcessEnter(@NotNull PsiFile file, @NotNull Editor editor, @NotNull DataContext dataContext) {
        return Result.Continue;
    }
}