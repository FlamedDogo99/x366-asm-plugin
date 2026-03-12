package com.x366.asm;

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesHandlerFactory;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class AsmFindUsagesHandlerFactory extends FindUsagesHandlerFactory {

    @Override
    public boolean canFindUsages(@NotNull PsiElement element) {
        if(!element.getLanguage().isKindOf(AsmLanguage.INSTANCE)) {
            return false;
        }
        var node = element.getNode();
        var type = node.getElementType();
        if(type == AsmTokenTypes.LABEL) {
            return true;
        }
        if(type == AsmTokenTypes.STATEMENT) {
            var first = node.getFirstChildNode();
            return first != null && first.getElementType() == AsmTokenTypes.LABEL;
        }
        return false;
    }

    @Override
    public FindUsagesHandler createFindUsagesHandler(@NotNull PsiElement element, boolean forHighlightUsages) {
        if(element.getNode().getElementType() == AsmTokenTypes.STATEMENT) {
            var first = element.getNode().getFirstChildNode();
            if(first != null && first.getElementType() == AsmTokenTypes.LABEL) {
                return new AsmFindUsagesHandler(first.getPsi());
            }
        }
        return new AsmFindUsagesHandler(element);
    }
}