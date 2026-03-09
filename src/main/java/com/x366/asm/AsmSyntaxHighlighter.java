package com.x366.asm;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class AsmSyntaxHighlighter extends SyntaxHighlighterBase {

    private static final Map<IElementType, TextAttributesKey> MAP = Map.ofEntries(
        Map.entry(AsmTokenTypes.COMMENT, AsmTextAttributeKeys.COMMENT),
        Map.entry(AsmTokenTypes.KEYWORD, AsmTextAttributeKeys.KEYWORD),
        Map.entry(AsmTokenTypes.SYSCALL, AsmTextAttributeKeys.SYSCALL),
        Map.entry(AsmTokenTypes.STRING, AsmTextAttributeKeys.STRING),
        Map.entry(AsmTokenTypes.NUMBER, AsmTextAttributeKeys.NUMBER),
        Map.entry(AsmTokenTypes.NUMBER_HEX, AsmTextAttributeKeys.NUMBER_HEX),
        Map.entry(AsmTokenTypes.NUMBER_BINARY, AsmTextAttributeKeys.NUMBER_BINARY),
        Map.entry(AsmTokenTypes.REGISTER, AsmTextAttributeKeys.REGISTER),
        Map.entry(AsmTokenTypes.LABEL, AsmTextAttributeKeys.LABEL),
        Map.entry(AsmTokenTypes.OPERATOR, AsmTextAttributeKeys.OPERATOR),
        Map.entry(AsmTokenTypes.DELIMITER, AsmTextAttributeKeys.DELIMITER),
        Map.entry(AsmTokenTypes.BAD_CHARACTER, AsmTextAttributeKeys.BAD_CHAR),
        Map.entry(AsmTokenTypes.DIRECTIVE, AsmTextAttributeKeys.DIRECTIVE)
    );

    @NotNull @Override
    public Lexer getHighlightingLexer() { return new AsmLexer(); }

    @Override
    @NotNull
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        TextAttributesKey key = MAP.get(tokenType);
        return key != null ? new TextAttributesKey[]{key} : TextAttributesKey.EMPTY_ARRAY;
    }
}
