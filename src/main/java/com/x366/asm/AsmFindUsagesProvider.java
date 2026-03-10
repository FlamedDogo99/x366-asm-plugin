package com.x366.asm;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AsmFindUsagesProvider implements FindUsagesProvider {

    @Override
    public @Nullable WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(new AsmLexer(), TokenSet.create(AsmTokenTypes.LABEL, AsmTokenTypes.IDENTIFIER), TokenSet.create(AsmTokenTypes.COMMENT), TokenSet.create(AsmTokenTypes.STRING));
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement element) {
        return element.getNode().getElementType() == AsmTokenTypes.LABEL;
    }

    @Override
    public @NotNull String getType(@NotNull PsiElement element) {
        return "label";
    }

    @Override
    public @NotNull String getDescriptiveName(@NotNull PsiElement element) {
        // we get a LeafPsiElement here
        String text = element.getText();
        return text.endsWith(":") ? text.substring(0, text.length() - 1) : text;
    }

    @Override
    public @NotNull String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return getDescriptiveName(element);
    }

    @Override
    public @Nullable String getHelpId(@NotNull PsiElement element) {
        return null;
    }
}