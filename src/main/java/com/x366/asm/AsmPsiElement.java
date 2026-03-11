package com.x366.asm;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.IElementType;
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
    public PsiElement setName(@NotNull String newName) {
        String oldName = getName();
        if(oldName == null || oldName.equals(newName)) {
            return this;
        }

        VirtualFile vFile = getVirtualFile();
        if(vFile == null) {
            return this;
        }

        Document doc = FileDocumentManager.getInstance().getDocument(vFile);
        if(doc == null) {
            return this;
        }

        // Walk the flat token list and replace only LABEL and IDENTIFIER tokens
        // that match the old name — avoids replacing text inside strings or comments.
        StringBuilder sb = new StringBuilder(doc.getText());
        int offset = 0; // cumulative shift as replacements change string length
        ASTNode node = getContainingFile().getNode().getFirstChildNode();
        while(node != null) {
            IElementType type = node.getElementType();
            if(type == AsmTokenTypes.LABEL || type == AsmTokenTypes.IDENTIFIER) {
                String tokenText = node.getText();
                // LABEL tokens include the trailing colon — strip it for comparison
                String tokenName = (type == AsmTokenTypes.LABEL && tokenText.endsWith(":")) ? tokenText.substring(0, tokenText.length() - 1) : tokenText;
                if(tokenName.equals(oldName)) {
                    int start = node.getStartOffset() + offset;
                    int end = start + tokenName.length(); // leave the colon untouched
                    sb.replace(start, end, newName);
                    offset += newName.length() - tokenName.length();
                }
            }
            node = node.getTreeNext();
        }
        doc.setText(sb);

        return this;
    }

    @Override
    public void navigate(boolean requestFocus) {
        VirtualFile vFile = getVirtualFile();
        if(vFile == null) {
            return;
        }
        new OpenFileDescriptor(getProject(), vFile, getNode().getStartOffset()).navigate(requestFocus);
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