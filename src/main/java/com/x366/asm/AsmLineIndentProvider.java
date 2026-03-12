package com.x366.asm;

import com.intellij.application.options.CodeStyle;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings.IndentOptions;
import com.intellij.psi.codeStyle.lineIndent.LineIndentProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AsmLineIndentProvider implements LineIndentProvider {

    @Nullable
    @Override
    public String getLineIndent(@NotNull Project project, @NotNull Editor editor, @Nullable Language language, int offset) {
        var doc = editor.getDocument();
        int lineNumber = doc.getLineNumber(offset);
        if(lineNumber == 0) {
            return null;
        }

        int prevLine = lineNumber - 1;
        while(prevLine >= 0) {
            int start = doc.getLineStartOffset(prevLine);
            int end = doc.getLineEndOffset(prevLine);
            String text = doc.getImmutableCharSequence().subSequence(start, end).toString().stripTrailing();
            if(!text.isBlank()) {
                String trimmed = text.stripLeading();

                if(trimmed.matches("(?i)(HLT|HALT|RET)\\b.*")) {
                    return "";
                }

                if(trimmed.matches("[a-zA-Z_]\\w*:\\s*")) {
                    String leading = text.substring(0, text.length() - trimmed.length());
                    IndentOptions opts = CodeStyle.getIndentOptions(project, editor.getDocument());
                    String indent = opts.USE_TAB_CHARACTER ? "\t" : " ".repeat(opts.INDENT_SIZE);
                    return leading + indent;
                }

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
        return language == null || language.isKindOf(AsmLanguage.INSTANCE);
    }
}