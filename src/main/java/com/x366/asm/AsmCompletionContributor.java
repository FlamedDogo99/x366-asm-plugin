package com.x366.asm;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AsmCompletionContributor extends CompletionContributor {

    private static final List<String> KEYWORDS = AsmLexer.KEYWORD_SET.stream().sorted().toList();
    private static final List<String> REGISTERS = AsmLexer.REGISTER_SET.stream().sorted().toList();
    private static final List<String> WORD_REGISTERS = AsmLexer.REGISTER_SET.stream().filter(r -> !r.endsWith("L")).sorted().toList();
    private static final List<String> SYSCALLS = AsmLexer.SYSCALL_SET.stream().sorted().toList();

    public AsmCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().withLanguage(AsmLanguage.INSTANCE).andNot(PlatformPatterns.psiElement().withElementType(AsmTokenTypes.COMMENT)), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {

                Document doc = parameters.getEditor().getDocument();
                int offset = parameters.getOffset();
                int lineStart = doc.getLineStartOffset(doc.getLineNumber(offset));
                String linePrefix = doc.getText().substring(lineStart, offset).stripLeading();

                if(linePrefix.indexOf(';') != -1) {
                    return;
                }

                String[] parts = linePrefix.split("[\\s,]+");
                int tokenIndex = parts.length;
                if(linePrefix.isEmpty() || linePrefix.matches(".*[\\s,]$")) {
                    tokenIndex++;
                }

                if(tokenIndex <= 1) {
                    for(String kw : KEYWORDS) {
                        result.addElement(LookupElementBuilder.create(kw).withTypeText("instruction").withBoldness(true));
                    }
                    return;
                }

                String instruction = parts[0].toUpperCase();
                if(AsmInstructionTable.FREE_FORM.contains(instruction)) {
                    return;
                }

                List<AsmInstructionTable.OperandKind> slots = AsmInstructionTable.OPERAND_SLOTS.get(instruction);
                if(slots == null) {
                    return;
                }

                int slotIndex = tokenIndex - 2;
                if(slotIndex >= slots.size()) {
                    return;
                }

                AsmInstructionTable.OperandKind kind = slots.get(slotIndex);
                VirtualFile vFile = parameters.getOriginalFile().getVirtualFile();

                switch(kind) {
                    case WORD_REG -> {
                        for(String reg : WORD_REGISTERS) {
                            result.addElement(LookupElementBuilder.create(reg).withTypeText("register"));
                        }
                    }
                    case REG_OR_IMM -> {
                        for(String reg : REGISTERS) {
                            result.addElement(LookupElementBuilder.create(reg).withTypeText("register"));
                        }
                    }
                    case LABEL -> {
                        if(vFile != null) {
                            for(String label : AsmLabelCache.getLabels(vFile.getPath(), doc)) {
                                result.addElement(LookupElementBuilder.create(label).withTypeText("label").withTailText(":"));
                            }
                        }
                    }
                    case SYSCALL_NAME -> {
                        for(String sys : SYSCALLS) {
                            result.addElement(LookupElementBuilder.create(sys).withTypeText("syscall").withItemTextItalic(true));
                        }
                    }
                }
            }
        });
    }
}