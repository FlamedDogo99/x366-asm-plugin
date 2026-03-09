package com.x366.asm;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;

public interface AsmTextAttributeKeys {
    TextAttributesKey COMMENT = createTextAttributesKey("ASM_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    TextAttributesKey KEYWORD = createTextAttributesKey("ASM_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    TextAttributesKey SYSCALL = createTextAttributesKey("ASM_SYSCALL", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
    TextAttributesKey STRING = createTextAttributesKey("ASM_STRING", DefaultLanguageHighlighterColors.STRING);
    TextAttributesKey NUMBER = createTextAttributesKey("ASM_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    TextAttributesKey NUMBER_HEX = createTextAttributesKey("ASM_NUMBER_HEX", DefaultLanguageHighlighterColors.NUMBER);
    TextAttributesKey NUMBER_BINARY = createTextAttributesKey("ASM_NUMBER_BINARY", DefaultLanguageHighlighterColors.NUMBER);
    TextAttributesKey REGISTER = createTextAttributesKey("ASM_REGISTER", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    TextAttributesKey LABEL = createTextAttributesKey("ASM_LABEL", DefaultLanguageHighlighterColors.CLASS_NAME);
    TextAttributesKey OPERATOR = createTextAttributesKey("ASM_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    TextAttributesKey DELIMITER = createTextAttributesKey("ASM_DELIMITER", DefaultLanguageHighlighterColors.BRACKETS);
    TextAttributesKey DIRECTIVE = createTextAttributesKey("ASM_DIRECTIVE", DefaultLanguageHighlighterColors.METADATA);
    TextAttributesKey BAD_CHAR = createTextAttributesKey("ASM_BAD_CHAR", DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);
}
