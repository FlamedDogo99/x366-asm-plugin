package com.x366.asm;

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ReadAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

public class AsmFindUsagesHandler extends FindUsagesHandler {

    public AsmFindUsagesHandler(@NotNull PsiElement element) {
        super(element);
    }

    @Override
    public boolean processElementUsages(@NotNull PsiElement element, @NotNull Processor<? super UsageInfo> processor, @NotNull com.intellij.find.findUsages.FindUsagesOptions options) {

        String labelText = element.getText();
        String labelName = labelText.endsWith(":") ? labelText.substring(0, labelText.length() - 1) : labelText;

        PsiFile file = element.getContainingFile();
        if(file == null) {
            return true;
        }

        ASTNode node = file.getNode().getFirstChildNode();
        while(node != null) {
            if(node.getElementType() == AsmTokenTypes.IDENTIFIER && node.getText().equals(labelName)) {
                ASTNode finalNode = node;
                boolean shouldContinue = ReadAction.compute(() -> {
                    UsageInfo usage = new UsageInfo(finalNode.getPsi());
                    return processor.process(usage);
                });
                if(!shouldContinue) {
                    return false;
                }
            }
            node = node.getTreeNext();
        }

        return true;
    }
}