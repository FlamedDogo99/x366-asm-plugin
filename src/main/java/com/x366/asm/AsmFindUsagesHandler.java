package com.x366.asm;

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ReadAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AsmFindUsagesHandler extends FindUsagesHandler {

    public AsmFindUsagesHandler(@NotNull PsiElement element) {
        super(element);
    }

    @Override
    public boolean processElementUsages(@NotNull PsiElement element, @NotNull Processor<? super UsageInfo> processor, @NotNull com.intellij.find.findUsages.FindUsagesOptions options) {
        String rawText = element.getText();
        String labelName = rawText.endsWith(":") ? rawText.substring(0, rawText.length() - 1) : rawText;

        PsiFile file = element.getContainingFile();
        if(file == null) {
            return true;
        }

        List<PsiElement> matches = ReadAction.compute(() -> {
            List<PsiElement> found = new ArrayList<>();
            ASTNode node = file.getNode().getFirstChildNode();
            while(node != null) {
                if(node.getElementType() == AsmTokenTypes.STATEMENT) {
                    ASTNode child = node.getFirstChildNode();
                    while(child != null) {
                        if(child.getElementType() == AsmTokenTypes.IDENTIFIER && child.getText().equals(labelName)) {
                            found.add(child.getPsi());
                        }
                        child = child.getTreeNext();
                    }
                }
                node = node.getTreeNext();
            }
            return found;
        });

        for(PsiElement match : matches) {
            if(!processor.process(new UsageInfo(match))) {
                return false;
            }
        }
        return true;
    }
}