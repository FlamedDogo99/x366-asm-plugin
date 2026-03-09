package com.x366.asm;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import java.util.Map;

public class AsmColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = {
        new AttributesDescriptor("Comment", AsmTextAttributeKeys.COMMENT),
        new AttributesDescriptor("Instruction", AsmTextAttributeKeys.KEYWORD),
        new AttributesDescriptor("Syscall", AsmTextAttributeKeys.SYSCALL),
        new AttributesDescriptor("String", AsmTextAttributeKeys.STRING),
        new AttributesDescriptor("Number", AsmTextAttributeKeys.NUMBER),
        new AttributesDescriptor("Register", AsmTextAttributeKeys.REGISTER),
        new AttributesDescriptor("Label", AsmTextAttributeKeys.LABEL),
        new AttributesDescriptor("Operator", AsmTextAttributeKeys.OPERATOR),
        new AttributesDescriptor("Delimiter", AsmTextAttributeKeys.DELIMITER),
        new AttributesDescriptor("Bad character", AsmTextAttributeKeys.BAD_CHAR),
        new AttributesDescriptor("Directive", AsmTextAttributeKeys.DIRECTIVE),
    };

    @NotNull @Override public String getDisplayName() {
      return "x366 ASM";
    }
    @Nullable @Override public Icon getIcon() { return null; }
    @NotNull @Override public SyntaxHighlighter getHighlighter() {
      return new AsmSyntaxHighlighter();
    }
    @NotNull @Override public AttributesDescriptor[] getAttributeDescriptors() {
      return DESCRIPTORS;
    }
    @NotNull @Override public ColorDescriptor[] getColorDescriptors() {
      return ColorDescriptor.EMPTY_ARRAY;
    }
    @Nullable @Override public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
      return null;
    }

    @NotNull @Override
    public String getDemoText() {
        return """
                ; x366 Assembly demo
                .MEMORY 8K
                start:
                    MOV AX, 42
                    MOV BX, 0xFF
                    ADD AX, BX
                    SYSCALL PRINT_INT
                    MOV CX, "hello"
                    JNE start
                    HLT
                """;
    }
}
