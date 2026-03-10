package com.x366.asm;

import com.intellij.lang.Language;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.codeStyle.lineIndent.LineIndentProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AsmLineIndentProvider implements LineIndentProvider {

    @Nullable
    @Override
    public String getLineIndent(@NotNull Project project, @NotNull Editor editor, @Nullable Language language, int offset) {
        if(offset == 0) {
            return null;
        }

        var doc = editor.getDocument();
        int lineNumber = doc.getLineNumber(offset);

        // check previous non-empty line
        int prevLine = lineNumber - 1;
        while(prevLine >= 0) {
            int start = doc.getLineStartOffset(prevLine);
            int end = doc.getLineEndOffset(prevLine);
            String text = doc.getText().substring(start, end).stripTrailing();
            if(!text.isBlank()) {
                String trimmed = text.stripLeading();

                // if previous line ends with a colon, indent
                if(trimmed.matches("[a-zA-Z_]\\w*:")) {
                    String leading = text.substring(0, text.length() - trimmed.length());
                    return leading + "    ";
                }

                // maintain previous indent otherwise
                int indent = 0;
                while(indent < text.length() && (text.charAt(indent) == ' ' || text.charAt(indent) == '\t')) {
                    indent++;
                }
                if(indent > 0) {
                    return text.substring(0, indent);
                }

                return null;
            }
            prevLine--;
        }
        return null;
    }

    @Override
    public boolean isSuitableFor(@Nullable Language language) {
        return language != null && language.isKindOf(AsmLanguage.INSTANCE);
    }
}