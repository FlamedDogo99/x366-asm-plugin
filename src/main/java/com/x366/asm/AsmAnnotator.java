package com.x366.asm;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class AsmAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if(element.getNode().getElementType() != AsmTokenTypes.IDENTIFIER) {
            return;
        }

        PsiFile file = element.getContainingFile();
        if(file == null) {
            return;
        }

        var vFile = file.getVirtualFile();
        if(vFile == null) {
            return;
        }

        Document doc = FileDocumentManager.getInstance().getDocument(vFile);
        if(doc == null) {
            return;
        }

        if(!AsmLabelCache.getLabels(vFile.getPath(), doc).contains(element.getText())) {
            holder.newAnnotation(HighlightSeverity.WARNING, "Unresolved label '" + element.getText() + "'").range(element).create();
        }
    }
}