package com.x366.asm;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class AsmReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement().withLanguage(AsmLanguage.INSTANCE),
            new PsiReferenceProvider() {
                @NotNull
                @Override
                public PsiReference[] getReferencesByElement(
                    @NotNull PsiElement element,
                    @NotNull ProcessingContext context) {

                    String text = element.getText();

                    // attach references to plain identifiers
                    if(!text.matches("[a-zA-Z_]\\w*")) {
                        return PsiReference.EMPTY_ARRAY;
                    }

                    // skip keywords registers and syscalls
                    String upper = text.toUpperCase();
                    if(AsmLexer.KEYWORD_SET.contains(upper) || AsmLexer.SYSCALL_SET.contains(upper) || AsmLexer.REGISTER_SET.contains(upper)) {
                        return PsiReference.EMPTY_ARRAY;
                    }

                    return new PsiReference[]{
                        new AsmLabelReference(element, new TextRange(0, text.length()))
                    };
                }
            }
        );
    }
}