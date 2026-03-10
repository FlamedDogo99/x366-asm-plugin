package com.x366.asm;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsmAnnotator implements Annotator {

    private static final Pattern LABEL_DEF_PATTERN = Pattern.compile("^[ \\t]*([a-zA-Z_]\\w*):", Pattern.MULTILINE);
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("[a-zA-Z_]\\w*");

    private record CacheEntry(long stamp, Set<String> labels) {
    }

    // ConcurrentHashMap to avoid data races -- annotators are called from multiple threads
    private static final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        // only check plain identifiers
        if(element.getNode().getElementType() != AsmTokenTypes.IDENTIFIER) {
            return;
        }
        String text = element.getText();

        if(!IDENTIFIER_PATTERN.matcher(text).matches()) {
            return;
        }

        String upper = text.toUpperCase();
        if(AsmLexer.KEYWORD_SET.contains(upper) || AsmLexer.SYSCALL_SET.contains(upper) || AsmLexer.REGISTER_SET.contains(upper)) {
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

        if(!getLabels(vFile.getPath(), doc).contains(text)) {
            holder.newAnnotation(HighlightSeverity.WARNING, "Unresolved label '" + text + "'").range(element).create();
        }
    }

    private static Set<String> getLabels(String filePath, Document doc) {
        long stamp = doc.getModificationStamp();
        CacheEntry entry = cache.get(filePath);
        if(entry != null && entry.stamp() == stamp) {
            return entry.labels();
        }

        Set<String> labels = new HashSet<>();
        Matcher matcher = LABEL_DEF_PATTERN.matcher(doc.getImmutableCharSequence());
        while(matcher.find()) {
            labels.add(matcher.group(1));
        }

        cache.put(filePath, new CacheEntry(stamp, labels));
        return labels;
    }
}