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

public class AsmFoldingBuilder extends FoldingBuilderEx {

    @Override
    @NotNull
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();

        foldCommentRuns(root, document, descriptors);

        ASTNode node = root.getNode().getFirstChildNode();
        while(node != null) {
            if(node.getElementType() == AsmTokenTypes.STATEMENT) {
                ASTNode firstToken = node.getFirstChildNode();
                if(firstToken != null && firstToken.getElementType() == AsmTokenTypes.LABEL) {
                    foldLabelSection(node, root, document, descriptors);
                }
            }
            node = node.getTreeNext();
        }

        return descriptors.toArray(FoldingDescriptor[]::new);
    }

    private void foldCommentRuns(@NotNull PsiElement root, @NotNull Document document, List<FoldingDescriptor> descriptors) {
        int lineCount = document.getLineCount();
        int runStart = -1;
        int runEnd = -1;
        String firstCommentText = "";
        int consecutiveComments = 0;

        for(int i = 0; i < lineCount; i++) {
            int start = document.getLineStartOffset(i);
            int end = document.getLineEndOffset(i);
            String line = document.getImmutableCharSequence().subSequence(start, end).toString().stripLeading();

            if(line.startsWith(";")) {
                if(consecutiveComments == 0) {
                    runStart = start;
                    firstCommentText = line;
                }
                runEnd = end;
                consecutiveComments++;
            } else {
                if(consecutiveComments >= 3) {
                    descriptors.add(new FoldingDescriptor(root.getNode(), new TextRange(runStart, runEnd), null, firstCommentText + " ..."));
                }
                consecutiveComments = 0;
                runStart = -1;
                firstCommentText = "";
            }
        }

        if(consecutiveComments >= 3) {
            descriptors.add(new FoldingDescriptor(root.getNode(), new TextRange(runStart, runEnd), null, firstCommentText + " ..."));
        }
    }

    private void foldLabelSection(ASTNode labelStmt, PsiElement root, Document document, List<FoldingDescriptor> descriptors) {
        ASTNode labelToken = labelStmt.getFirstChildNode();
        if(labelToken == null) {
            return;
        }

        String labelText = labelToken.getText();
        String labelName = labelText.endsWith(":") ? labelText.substring(0, labelText.length() - 1) : labelText;

        int sectionStart = labelStmt.getStartOffset() + labelStmt.getTextLength();

        ASTNode cursor = labelStmt.getTreeNext();
        ASTNode lastContent = null;
        while(cursor != null) {
            if(cursor.getElementType() == AsmTokenTypes.STATEMENT) {
                ASTNode first = cursor.getFirstChildNode();
                if(first != null && first.getElementType() == AsmTokenTypes.LABEL) {
                    break;
                }
                lastContent = cursor;
            }
            cursor = cursor.getTreeNext();
        }

        if(lastContent == null) {
            return;
        }

        int sectionEnd = lastContent.getStartOffset() + lastContent.getTextLength();
        if(sectionEnd <= sectionStart) {
            return;
        }
        if(document.getLineNumber(sectionEnd) - document.getLineNumber(sectionStart) < 1) {
            return;
        }

        descriptors.add(new FoldingDescriptor(root.getNode(), new TextRange(labelStmt.getStartOffset(), sectionEnd), null, labelName + ": ..."));
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