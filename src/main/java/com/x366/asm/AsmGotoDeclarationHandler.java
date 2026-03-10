package com.x366.asm;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.Nullable;

public class AsmGotoDeclarationHandler implements GotoDeclarationHandler {
    @Nullable
    @Override
    public PsiElement[] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        if(sourceElement == null) {
            return null;
        }
        if(!sourceElement.getLanguage().isKindOf(AsmLanguage.INSTANCE)) {
            return null;
        }
        // only need to handle identifier here
        if(sourceElement.getNode().getElementType() != AsmTokenTypes.IDENTIFIER) {
            return null;
        }

        String name = sourceElement.getText();
        PsiFile file = sourceElement.getContainingFile();
        if(file == null) {
            return null;
        }

        ASTNode node = file.getNode().getFirstChildNode();
        while(node != null) {
            if(node.getElementType() == AsmTokenTypes.LABEL) {
                String labelText = node.getText();
                String labelName = labelText.endsWith(":") ? labelText.substring(0, labelText.length() - 1) : labelText;
                if(labelName.equals(name)) {
                    return new PsiElement[]{node.getPsi()};
                }
            }
            node = node.getTreeNext();
        }

        return null;
    }
}