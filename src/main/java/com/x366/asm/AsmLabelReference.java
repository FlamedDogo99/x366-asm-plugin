package com.x366.asm;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.lang.ASTNode;

public class AsmLabelReference extends PsiReferenceBase<PsiElement> {

    public AsmLabelReference(@NotNull PsiElement element, TextRange rangeInElement) {
        super(element, rangeInElement);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiFile file = myElement.getContainingFile();
        if(file == null) {
            return null;
        }

        String targetName = myElement.getText();

        ASTNode node = file.getNode().getFirstChildNode();
        while(node != null) {
            if(node.getElementType() == AsmTokenTypes.LABEL) {
                String labelText = node.getText();
                String labelName = labelText.endsWith(":") ? labelText.substring(0, labelText.length() - 1) : labelText;
                if(labelName.equals(targetName)) {
                    return node.getPsi();
                }
            }
            node = node.getTreeNext();
        }
        return null;
    }

    // Override isReferenceTo() to compare by file + text offset rather than PSI instance
    // identity. The default implementation uses areElementsEquivalent(resolve(), element)
    // which relies on instance equality -- node.getPsi() is cached per ASTNode but the
    // element passed in by Find Usages may be a different wrapper instance for the same
    // node, causing matches to be missed.
    @Override
    public boolean isReferenceTo(@NotNull PsiElement element) {
        // IntelliJ passes the LEAF node at the cursor, which is a plain LeafPsiElement
        // not an AsmPsiElement -- so we cannot use instanceof here.
        if(element.getNode().getElementType() != AsmTokenTypes.LABEL) {
            return false;
        }

        // Compare by name: does this reference point to a label with the same name?
        String refText = myElement.getText();
        String labelText = element.getText();
        String labelName = labelText.endsWith(":") ? labelText.substring(0, labelText.length() - 1) : labelText;
        if(!refText.equals(labelName)) {
            return false;
        }

        // Confirm they are in the same file
        PsiFile refFile = myElement.getContainingFile();
        PsiFile labelFile = element.getContainingFile();
        if(refFile == null || labelFile == null) {
            return false;
        }
        return refFile.equals(labelFile);
    }

    @Override
    @NotNull
    public Object[] getVariants() {
        return new Object[0];
    }
}