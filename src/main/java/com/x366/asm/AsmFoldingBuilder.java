package com.x366.asm;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsmFoldingBuilder extends FoldingBuilderEx {

    private static final Pattern COMMENT_LINE = Pattern.compile("^[ \\t]*;.*$", Pattern.MULTILINE);
    private static final Pattern LABEL_LINE = Pattern.compile("^([a-zA-Z_]\\w*):", Pattern.MULTILINE);

    @Override
    @NotNull
    public FoldingDescriptor[] buildFoldRegions(
        @NotNull PsiElement root,
        @NotNull Document document,
        boolean quick) {

        List<FoldingDescriptor> descriptors = new ArrayList<>();
        String text = document.getText();
        int docLen = text.length();

        // fold comment only lines
        Matcher cm = COMMENT_LINE.matcher(text);
        int blockStart = -1;
        int blockEnd = -1;
        int blockLines = 0;

        while(cm.find()) {
            int lineStart = cm.start();
            int lineEnd = cm.end();

            if(blockStart == -1) {
                blockStart = lineStart;
                blockEnd = lineEnd;
                blockLines = 1;
            } else {
                // Check from previous match to this one is white space
                String gap = text.substring(blockEnd, lineStart);
                if(gap.isBlank()) {
                    blockEnd = lineEnd;
                    blockLines++;
                } else {
                    if(blockLines >= 3) {
                        addCommentFold(descriptors, root, text, blockStart, blockEnd, docLen);
                    }
                    blockStart = lineStart;
                    blockEnd = lineEnd;
                    blockLines = 1;
                }
            }
        }
        if(blockLines >= 3) {
            addCommentFold(descriptors, root, text, blockStart, blockEnd, docLen);
        }

        // fold label sections
        Matcher lm = LABEL_LINE.matcher(text);
        List<int[]> labelPositions = new ArrayList<>();
        while(lm.find()) {
            labelPositions.add(new int[]{lm.start(), lm.end()});
        }

        for(int i = 0; i < labelPositions.size(); i++) {
            int sectionStart = labelPositions.get(i)[1]; // end of label
            int sectionEnd = (i + 1 < labelPositions.size())
                ? labelPositions.get(i + 1)[0]          // next label start
                : docLen;

            // trim whitespace
            while(sectionEnd > sectionStart && Character.isWhitespace(text.charAt(sectionEnd - 1))) {
                sectionEnd--;
            }

            if(sectionEnd > sectionStart + 1) {
                String labelName = text.substring(labelPositions.get(i)[0],
                    text.indexOf(':', labelPositions.get(i)[0]));
                int foldStart = labelPositions.get(i)[0];
                descriptors.add(new FoldingDescriptor(
                    root.getNode(),
                    new TextRange(foldStart, sectionEnd),
                    null,
                    labelName + ": ..."
                ));
            }
        }

        return descriptors.toArray(FoldingDescriptor[]::new);
    }

    private void addCommentFold(List<FoldingDescriptor> descriptors, PsiElement root,
                                String text, int start, int end, int docLen) {
        if(end > docLen) {
            end = docLen;
        }
        if(end <= start) {
            return;
        }
        // first line as placeholder text
        int firstNewline = text.indexOf('\n', start);
        String firstLine = text.substring(start, firstNewline > 0 ? firstNewline : end).strip();
        descriptors.add(new FoldingDescriptor(
            root.getNode(),
            new TextRange(start, end),
            null,
            firstLine + " ..."
        ));
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        return "...";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }
}