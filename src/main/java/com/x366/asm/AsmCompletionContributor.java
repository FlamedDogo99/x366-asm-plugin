package com.x366.asm;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsmCompletionContributor extends CompletionContributor {

    private static final Pattern LABEL_PATTERN = Pattern.compile("^\\s*([a-zA-Z_]\\w*):", Pattern.MULTILINE);

    private static final List<String> KEYWORDS = AsmLexer.KEYWORD_SET.stream().sorted().toList();
    private static final List<String> REGISTERS = AsmLexer.REGISTER_SET.stream().sorted().toList();
    private static final List<String> SYSCALLS = AsmLexer.SYSCALL_SET.stream().sorted().toList();

    public AsmCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().withLanguage(AsmLanguage.INSTANCE), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {

                Document doc = parameters.getEditor().getDocument();
                int offset = parameters.getOffset();
                int lineStart = doc.getLineStartOffset(doc.getLineNumber(offset));
                String linePrefix = doc.getText().substring(lineStart, offset).stripLeading();

                String[] parts = linePrefix.split("[\\s,]+");
                int tokenIndex = parts.length;
                if(linePrefix.isEmpty() || linePrefix.matches(".*[\\s,]$")) {
                    tokenIndex++;
                }

                if(tokenIndex <= 1) {
                    for(String kw : KEYWORDS) {
                        result.addElement(LookupElementBuilder.create(kw)
                            .withTypeText("instruction").withBoldness(true));
                    }
                } else {
                    String firstToken = parts.length > 0 ? parts[0].toUpperCase() : "";

                    if(firstToken.equals("SYSCALL")) {
                        for(String sys : SYSCALLS) {
                            result.addElement(LookupElementBuilder.create(sys)
                                .withTypeText("syscall").withItemTextItalic(true));
                        }
                    } else {
                        for(String reg : REGISTERS) {
                            result.addElement(LookupElementBuilder.create(reg)
                                .withTypeText("register"));
                        }
                        Matcher matcher = LABEL_PATTERN.matcher(doc.getText());
                        while(matcher.find()) {
                            result.addElement(LookupElementBuilder.create(matcher.group(1))
                                .withTypeText("label").withTailText(":"));
                        }
                    }
                }
            }
        });
    }
}