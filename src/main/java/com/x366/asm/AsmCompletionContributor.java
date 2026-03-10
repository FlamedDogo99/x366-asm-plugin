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
        extend(CompletionType.BASIC,
            PlatformPatterns.psiElement().withLanguage(AsmLanguage.INSTANCE),
            new CompletionProvider<>() {
                @Override
                protected void addCompletions(@NotNull CompletionParameters parameters,
                                              @NotNull ProcessingContext context,
                                              @NotNull CompletionResultSet result) {

                    // keywords and instructions
                    for(String kw : KEYWORDS) {
                        result.addElement(
                            LookupElementBuilder.create(kw)
                                .withTypeText("instruction")
                                .withBoldness(true)
                        );
                    }

                    // registers
                    for(String reg : REGISTERS) {
                        result.addElement(
                            LookupElementBuilder.create(reg)
                                .withTypeText("register")
                        );
                    }

                    // syscalls
                    for(String sys : SYSCALLS) {
                        result.addElement(
                            LookupElementBuilder.create(sys)
                                .withTypeText("syscall")
                                .withItemTextItalic(true)
                        );
                    }

                    // labels
                    Document doc = parameters.getEditor().getDocument();
                    String text = doc.getText();
                    Matcher matcher = LABEL_PATTERN.matcher(text);
                    while(matcher.find()) {
                        String labelName = matcher.group(1);
                        result.addElement(
                            LookupElementBuilder.create(labelName)
                                .withTypeText("label")
                                .withTailText(":")
                        );
                    }
                }
            }
        );
    }
}