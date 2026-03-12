package com.x366.asm;

import com.intellij.psi.tree.IElementType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Shared instruction metadata used by both AsmAnnotator (validation)
 * and AsmCompletionContributor (completions).
 */
public class AsmInstructionTable {

    public enum OperandKind {
        WORD_REG, // word registers
        REG_OR_IMM, // register or immediate
        LABEL, // jump target label
        SYSCALL_NAME // syscall identifiers
    }

    // map instruction to param kinds
    public static final Map<String, List<OperandKind>> OPERAND_SLOTS = Map.ofEntries(Map.entry("MOV", List.of(OperandKind.REG_OR_IMM, OperandKind.REG_OR_IMM)), Map.entry("LEA", List.of(OperandKind.WORD_REG)),

        Map.entry("ADD", List.of(OperandKind.WORD_REG, OperandKind.REG_OR_IMM)), Map.entry("SUB", List.of(OperandKind.WORD_REG, OperandKind.REG_OR_IMM)), Map.entry("CMP", List.of(OperandKind.WORD_REG, OperandKind.REG_OR_IMM)), Map.entry("AND", List.of(OperandKind.WORD_REG, OperandKind.REG_OR_IMM)), Map.entry("OR", List.of(OperandKind.WORD_REG, OperandKind.REG_OR_IMM)), Map.entry("XOR", List.of(OperandKind.WORD_REG, OperandKind.REG_OR_IMM)), Map.entry("SHL", List.of(OperandKind.WORD_REG, OperandKind.REG_OR_IMM)), Map.entry("SHR", List.of(OperandKind.WORD_REG, OperandKind.REG_OR_IMM)), Map.entry("TEST", List.of(OperandKind.WORD_REG, OperandKind.REG_OR_IMM)), Map.entry("INC", List.of(OperandKind.WORD_REG)), Map.entry("DEC", List.of(OperandKind.WORD_REG)), Map.entry("MUL", List.of(OperandKind.WORD_REG)), Map.entry("DIV", List.of(OperandKind.WORD_REG)), Map.entry("NOT", List.of(OperandKind.WORD_REG)),

        Map.entry("PUSH", List.of(OperandKind.WORD_REG)), Map.entry("POP", List.of(OperandKind.WORD_REG)),

        Map.entry("JMP", List.of(OperandKind.LABEL)), Map.entry("JE", List.of(OperandKind.LABEL)), Map.entry("JNE", List.of(OperandKind.LABEL)), Map.entry("JZ", List.of(OperandKind.LABEL)), Map.entry("JNZ", List.of(OperandKind.LABEL)), Map.entry("JG", List.of(OperandKind.LABEL)), Map.entry("JGE", List.of(OperandKind.LABEL)), Map.entry("JL", List.of(OperandKind.LABEL)), Map.entry("JLE", List.of(OperandKind.LABEL)), Map.entry("JA", List.of(OperandKind.LABEL)), Map.entry("JAE", List.of(OperandKind.LABEL)), Map.entry("JB", List.of(OperandKind.LABEL)), Map.entry("JBE", List.of(OperandKind.LABEL)), Map.entry("LOOP", List.of(OperandKind.LABEL)), Map.entry("CALL", List.of(OperandKind.LABEL)),

        Map.entry("SYSCALL", List.of(OperandKind.SYSCALL_NAME)),

        Map.entry("RET", List.of()), Map.entry("NOP", List.of()), Map.entry("HLT", List.of()), Map.entry("HALT", List.of()));

    public static final Set<String> FREE_FORM = Set.of("DB", "DW", "DUP", "MOVB");

    public static final Set<IElementType> OPERAND_TYPES = Set.of(AsmTokenTypes.REGISTER, AsmTokenTypes.IDENTIFIER, AsmTokenTypes.NUMBER, AsmTokenTypes.NUMBER_HEX, AsmTokenTypes.NUMBER_BINARY, AsmTokenTypes.STRING, AsmTokenTypes.KEYWORD   // e.g. DUP used inside DB operands
    );

    public static final Set<String> WORD_REGISTER_NAMES = AsmLexer.REGISTER_SET.stream().filter(r -> !r.endsWith("L")).collect(java.util.stream.Collectors.toUnmodifiableSet());

    private AsmInstructionTable() {
    }
}