package com.x366.asm;

import com.intellij.openapi.editor.Document;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsmLabelCache {

    private static final Pattern LABEL_DEF_PATTERN = Pattern.compile("^[ \\t]*([a-zA-Z_]\\w*):", Pattern.MULTILINE);

    private record CacheEntry(long stamp, Set<String> labels) {
    }

    private static final int MAX_CACHE = 50;
    private static final Map<String, CacheEntry> cache = Collections.synchronizedMap(new java.util.LinkedHashMap<>(MAX_CACHE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
            return size() > MAX_CACHE;
        }
    });

    private AsmLabelCache() {
    }

    public static Set<String> getLabels(String filePath, Document doc) {
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