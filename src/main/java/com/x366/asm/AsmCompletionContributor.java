package com.x366.asm;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class AsmCompletionContributor extends CompletionContributor {

    private static final List<String> KEYWORDS = AsmLexer.KEYWORD_SET.stream().sorted().toList();
    private static final List<String> REGISTERS = AsmLexer.REGISTER_SET.stream().sorted().toList();
    private static final List<String> WORD_REGISTERS = AsmLexer.REGISTER_SET.stream().filter(r -> !r.endsWith("L")).sorted().toList();
    private static final List<String> SYSCALLS = AsmLexer.SYSCALL_SET.stream().sorted().toList();

    private enum Kind {
        WORD_REG, // word registers
        REG_OR_IMM, // register or immediate
        LABEL, // jump target label
        SYSCALL_NAME // syscall identifiers
    }

    // map instruction to param slot kinds
    private static final Map<String, List<Kind>> OPERAND_SLOTS = Map.ofEntries(Map.entry("MOV", List.of(Kind.REG_OR_IMM, Kind.REG_OR_IMM)), Map.entry("LEA", List.of(Kind.WORD_REG)), Map.entry("ADD", List.of(Kind.WORD_REG, Kind.REG_OR_IMM)), Map.entry("SUB", List.of(Kind.WORD_REG, Kind.REG_OR_IMM)), Map.entry("CMP", List.of(Kind.WORD_REG, Kind.REG_OR_IMM)), Map.entry("AND", List.of(Kind.WORD_REG, Kind.REG_OR_IMM)), Map.entry("OR", List.of(Kind.WORD_REG, Kind.REG_OR_IMM)), Map.entry("XOR", List.of(Kind.WORD_REG, Kind.REG_OR_IMM)), Map.entry("SHL", List.of(Kind.WORD_REG, Kind.REG_OR_IMM)), Map.entry("SHR", List.of(Kind.WORD_REG, Kind.REG_OR_IMM)), Map.entry("TEST", List.of(Kind.WORD_REG, Kind.REG_OR_IMM)), Map.entry("INC", List.of(Kind.WORD_REG)), Map.entry("DEC", List.of(Kind.WORD_REG)), Map.entry("MUL", List.of(Kind.WORD_REG)), Map.entry("DIV", List.of(Kind.WORD_REG)), Map.entry("NOT", List.of(Kind.WORD_REG)), Map.entry("PUSH", List.of(Kind.WORD_REG)), Map.entry("POP", List.of(Kind.WORD_REG)), Map.entry("JMP", List.of(Kind.LABEL)), Map.entry("JE", List.of(Kind.LABEL)), Map.entry("JNE", List.of(Kind.LABEL)), Map.entry("JZ", List.of(Kind.LABEL)), Map.entry("JNZ", List.of(Kind.LABEL)), Map.entry("JG", List.of(Kind.LABEL)), Map.entry("JGE", List.of(Kind.LABEL)), Map.entry("JL", List.of(Kind.LABEL)), Map.entry("JLE", List.of(Kind.LABEL)), Map.entry("JA", List.of(Kind.LABEL)), Map.entry("JAE", List.of(Kind.LABEL)), Map.entry("JB", List.of(Kind.LABEL)), Map.entry("JBE", List.of(Kind.LABEL)), Map.entry("LOOP", List.of(Kind.LABEL)), Map.entry("CALL", List.of(Kind.LABEL)), Map.entry("SYSCALL", List.of(Kind.SYSCALL_NAME)));

    public AsmCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().withLanguage(AsmLanguage.INSTANCE).andNot(PlatformPatterns.psiElement().withElementType(AsmTokenTypes.COMMENT)), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {

                Document doc = parameters.getEditor().getDocument();
                int offset = parameters.getOffset();
                int lineStart = doc.getLineStartOffset(doc.getLineNumber(offset));
                String linePrefix = doc.getText().substring(lineStart, offset).stripLeading();

                // don't show completions in comments
                if(linePrefix.indexOf(';') != -1) {
                    return;
                }

                String[] parts = linePrefix.split("[\\s,]+");
                int tokenIndex = parts.length;
                if(linePrefix.isEmpty() || linePrefix.matches(".*[\\s,]$")) {
                    tokenIndex++;
                }

                // position 1, show instructions
                if(tokenIndex <= 1) {
                    for(String kw : KEYWORDS) {
                        result.addElement(LookupElementBuilder.create(kw).withTypeText("instruction").withBoldness(true));
                    }
                    return;
                }

                String instruction = parts[0].toUpperCase();
                List<Kind> slots = OPERAND_SLOTS.get(instruction);

                if(slots == null) {
                    return;
                }
                int slotIndex = tokenIndex - 2;
                if(slotIndex >= slots.size()) {
                    return;
                }

                Kind kind = slots.get(slotIndex);
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