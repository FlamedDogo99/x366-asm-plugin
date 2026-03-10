package com.x366.asm;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsmLabelReference extends PsiReferenceBase<PsiElement> {

    private final Pattern labelPattern;

    public AsmLabelReference(@NotNull PsiElement element, TextRange rangeInElement) {
        super(element, rangeInElement);
        String labelName = element.getText().substring(rangeInElement.getStartOffset(), rangeInElement.getEndOffset());
        this.labelPattern = Pattern.compile("^[ \\t]*(" + Pattern.quote(labelName) + "):", Pattern.MULTILINE);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiFile file = myElement.getContainingFile();
        if(file == null) {
            return null;
        }

        var vf = file.getVirtualFile();
        if(vf == null) {
            return null;
        }
        Document doc = FileDocumentManager.getInstance().getDocument(vf);
        if(doc == null) {
            return null;
        }

        // find "labelName:" at the start of a line
        Matcher matcher = labelPattern.matcher(doc.getText());

        while(matcher.find()) {
            int labelOffset = matcher.start(1);
            // stop from matching itself
            if(labelOffset != myElement.getTextOffset()) {
                return file.findElementAt(labelOffset);
            }
        }
        return null;
    }

    @Override
    @NotNull
    public Object[] getVariants() {
        return new Object[0];
    }
}