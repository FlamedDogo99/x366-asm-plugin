package com.x366.asm;

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesHandlerFactory;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class AsmFindUsagesHandlerFactory extends FindUsagesHandlerFactory {

    @Override
    public boolean canFindUsages(@NotNull PsiElement element) {
        return element.getLanguage().isKindOf(AsmLanguage.INSTANCE)
            && element.getNode().getElementType() == AsmTokenTypes.LABEL;
    }

    @Override
    public FindUsagesHandler createFindUsagesHandler(
        @NotNull PsiElement element,
        boolean forHighlightUsages) {
        return new AsmFindUsagesHandler(element);
    }
}