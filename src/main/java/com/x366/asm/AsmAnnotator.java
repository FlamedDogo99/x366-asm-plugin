package com.x366.asm;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AsmAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        var type = element.getNode().getElementType();

        if(type == AsmTokenTypes.IDENTIFIER) {
            annotateIdentifier(element, holder);
            return;
        }

        if(type == AsmTokenTypes.STATEMENT) {
            annotateStatement(element, holder);
        }
    }

    private void annotateIdentifier(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
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

    private void annotateStatement(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        ASTNode stmt = element.getNode();

        ASTNode instrNode = stmt.getFirstChildNode();
        if(instrNode == null) {
            return;
        }

        // label, instruction
        ASTNode cursor = instrNode;
        if(cursor.getElementType() == AsmTokenTypes.LABEL) {
            cursor = cursor.getTreeNext();
            if(cursor == null) {
                return;
            }
        }

        if(cursor.getElementType() != AsmTokenTypes.KEYWORD) {
            return;
        }

        String instruction = cursor.getText().toUpperCase();

        if(AsmInstructionTable.FREE_FORM.contains(instruction)) {
            return;
        }

        List<AsmInstructionTable.OperandKind> slots = AsmInstructionTable.OPERAND_SLOTS.get(instruction);
        if(slots == null) {
            return;
        }

        List<ASTNode> operands = collectOperands(cursor.getTreeNext());

        int expected = slots.size();
        int actual = operands.size();

        if(actual > expected) {
            for(int i = expected; i < operands.size(); i++) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Unexpected operand: '" + instruction + "' takes " + expected + (expected == 1 ? " operand" : " operands")).range(operands.get(i).getPsi()).create();
            }
            return;
        }

        if(actual < expected) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Too few operands: '" + instruction + "' expects " + expected + (expected == 1 ? " operand" : " operands") + ", got " + actual).range(cursor.getPsi()).create();
            return;
        }

        for(int i = 0; i < slots.size(); i++) {
            AsmInstructionTable.OperandKind kind = slots.get(i);
            ASTNode operand = operands.get(i);
            validateOperand(operand, kind, holder);
        }
    }

    private List<ASTNode> collectOperands(@Nullable ASTNode start) {
        List<ASTNode> operands = new ArrayList<>();
        ASTNode node = start;

        while(node != null) {
            var t = node.getElementType();

            if(t == AsmTokenTypes.COMMENT) {
                break; // inline comment end operand
            }

            if(t == AsmTokenTypes.DELIMITER) {
                String text = node.getText();
                if(text.equals("[")) {
                    // memory expression
                    ASTNode memStart = node;
                    while(node != null && !node.getText().equals("]")) {
                        node = node.getTreeNext();
                    }
                    operands.add(memStart);
                }
            } else if(AsmInstructionTable.OPERAND_TYPES.contains(t)) {
                operands.add(node);
            }

            node = node != null ? node.getTreeNext() : null;
        }

        return operands;
    }

    private void validateOperand(@NotNull ASTNode operand, @NotNull AsmInstructionTable.OperandKind kind, @NotNull AnnotationHolder holder) {
        var t = operand.getElementType();
        String text = operand.getText().toUpperCase();

        if(t == AsmTokenTypes.DELIMITER && text.equals("[")) {
            return;
        }

        switch(kind) {
            case WORD_REG -> {
                if(t != AsmTokenTypes.REGISTER) {
                    holder.newAnnotation(HighlightSeverity.WARNING, "Expected a register, got '" + operand.getText() + "'").range(operand.getPsi()).create();
                } else if(!AsmInstructionTable.WORD_REGISTER_NAMES.contains(text)) {
                    holder.newAnnotation(HighlightSeverity.WARNING, "Expected a word register (e.g. AX), byte register '" + operand.getText() + "' not valid here").range(operand.getPsi()).create();
                }
            }
            case REG_OR_IMM -> {
                boolean isReg = t == AsmTokenTypes.REGISTER;
                boolean isImm = t == AsmTokenTypes.NUMBER || t == AsmTokenTypes.NUMBER_HEX || t == AsmTokenTypes.NUMBER_BINARY || t == AsmTokenTypes.STRING;
                boolean isLabel = t == AsmTokenTypes.IDENTIFIER;
                // registers, immediates, and labels
                if(!isReg && !isImm && !isLabel) {
                    holder.newAnnotation(HighlightSeverity.WARNING, "Expected a register, immediate, or label reference, got '" + operand.getText() + "'").range(operand.getPsi()).create();
                }
            }
            case LABEL -> {
                // identifiers
                if(t != AsmTokenTypes.IDENTIFIER) {
                    holder.newAnnotation(HighlightSeverity.WARNING, "Expected a label, got '" + operand.getText() + "'").range(operand.getPsi()).create();
                }
            }
            case SYSCALL_NAME -> {
                if(t != AsmTokenTypes.SYSCALL) {
                    holder.newAnnotation(HighlightSeverity.WARNING, "Expected a syscall name, got '" + operand.getText() + "'").range(operand.getPsi()).create();
                }
            }
        }
    }
}