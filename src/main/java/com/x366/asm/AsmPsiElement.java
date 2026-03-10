package com.x366.asm;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AsmPsiElement extends ASTWrapperPsiElement implements PsiNamedElement, NavigatablePsiElement {
    public AsmPsiElement(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @Nullable String getName() {
        if(getNode().getElementType() == AsmTokenTypes.LABEL) {
            String text = getText();
            return text.endsWith(":") ? text.substring(0, text.length() - 1) : text;
        }
        return getText();
    }

    @Override
    public PsiElement setName(@NotNull String name) {
        return this;
    }

    // Required so that resolve() returning this element actually causes the editor
    // to jump to it. ASTWrapperPsiElement does not implement Navigatable, so without
    // this override Cmd+click on a label invocation silently does nothing.
    @Override
    public void navigate(boolean requestFocus) {
        VirtualFile vFile = getVirtualFile();
        if(vFile == null) {
            return;
        }
        new OpenFileDescriptor(getProject(), vFile, getNode().getStartOffset())
            .navigate(requestFocus);
    }

    @Override
    public boolean canNavigate() {
        return getVirtualFile() != null;
    }

    @Override
    public boolean canNavigateToSource() {
        return canNavigate();
    }

    private @Nullable VirtualFile getVirtualFile() {
        var file = getContainingFile();
        if(file == null) {
            return null;
        }
        var vFile = file.getVirtualFile();
        return vFile != null ? vFile : file.getOriginalFile().getVirtualFile();
    }
}