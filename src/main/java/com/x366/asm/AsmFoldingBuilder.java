package com.x366.asm;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AsmFoldingBuilder extends FoldingBuilderEx {

    @Override
    @NotNull
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        List<ASTNode> commentRun = new ArrayList<>();

        ASTNode node = root.getNode().getFirstChildNode();
        while(node != null) {
            if(node.getElementType() == AsmTokenTypes.STATEMENT) {
                ASTNode firstToken = node.getFirstChildNode();

                if(firstToken != null && firstToken.getElementType() == AsmTokenTypes.COMMENT) {
                    commentRun.add(firstToken);
                } else {
                    flushCommentRun(commentRun, root, descriptors);
                    commentRun.clear();

                    if(firstToken != null && firstToken.getElementType() == AsmTokenTypes.LABEL) {
                        foldLabelSection(node, root, document, descriptors);
                    }
                }
            }
            node = node.getTreeNext();
        }
        flushCommentRun(commentRun, root, descriptors);

        return descriptors.toArray(FoldingDescriptor[]::new);
    }

    private void flushCommentRun(List<ASTNode> run, PsiElement root, List<FoldingDescriptor> descriptors) {
        if(run.size() < 3) {
            return;
        }
        ASTNode first = run.get(0);
        ASTNode last = run.get(run.size() - 1);
        TextRange range = new TextRange(first.getStartOffset(), last.getStartOffset() + last.getTextLength());
        String placeholder = first.getText().strip() + " ...";
        descriptors.add(new FoldingDescriptor(root.getNode(), range, null, placeholder));
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