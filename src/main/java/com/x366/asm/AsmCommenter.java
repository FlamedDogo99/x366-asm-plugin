package com.x366.asm;

import com.intellij.lang.CodeDocumentationAwareCommenter;
import com.intellij.psi.PsiComment;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;

public class AsmCommenter implements CodeDocumentationAwareCommenter {

    @Nullable
    @Override
    public String getLineCommentPrefix() {
        return "; ";
    }

    @Nullable
    @Override
    public String getBlockCommentPrefix() {
        return null;
    }

    @Nullable
    @Override
    public String getBlockCommentSuffix() {
        return null;
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentPrefix() {
        return null;
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentSuffix() {
        return null;
    }

    @Nullable
    @Override
    public String getDocumentationCommentPrefix() {
        return null;
    }

    @Nullable
    @Override
    public String getDocumentationCommentLinePrefix() {
        return null;
    }

    @Nullable
    @Override
    public String getDocumentationCommentSuffix() {
        return null;
    }

    @Override
    public boolean isDocumentationComment(PsiComment element) {
        return false;
    }

    @Override
    public IElementType getLineCommentTokenType() {
        return AsmTokenTypes.COMMENT;
    }

    @Override
    public IElementType getBlockCommentTokenType() {
        return null;
    }

    @Override
    public IElementType getDocumentationCommentTokenType() {
        return null;
    }
}