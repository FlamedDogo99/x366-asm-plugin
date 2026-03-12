package com.x366.asm;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;

public interface AsmTokenTypes {
    IElementType COMMENT = new AsmElementType("COMMENT");
    IElementType KEYWORD = new AsmElementType("KEYWORD");
    IElementType SYSCALL = new AsmElementType("SYSCALL");
    IElementType STRING = new AsmElementType("STRING");
    IElementType NUMBER = new AsmElementType("NUMBER");
    IElementType NUMBER_HEX = new AsmElementType("NUMBER_HEX");
    IElementType NUMBER_BINARY = new AsmElementType("NUMBER_BINARY");
    IElementType REGISTER = new AsmElementType("REGISTER");
    IElementType LABEL = new AsmElementType("LABEL");
    IElementType DIRECTIVE = new AsmElementType("DIRECTIVE");
    IElementType IDENTIFIER = new AsmElementType("IDENTIFIER");
    IElementType OPERATOR = new AsmElementType("OPERATOR");
    IElementType DELIMITER = new AsmElementType("DELIMITER");
    IElementType BAD_CHARACTER = new AsmElementType("BAD_CHARACTER");
    IElementType SPACE = new AsmElementType("SPACE");
    IElementType NEWLINE = new AsmElementType("NEWLINE");
}