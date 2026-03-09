package com.x366.asm;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class AsmLexer extends LexerBase {

    private static final Set<String> KEYWORDS = Set.of(
        "MOV","MOVB","ADD","SUB","MUL","DIV","INC","DEC",
        "AND","OR","XOR","NOT","SHL","SHR","CMP","TEST",
        "JMP","JE","JNE","JZ","JNZ","JG","JGE","JL","JLE",
        "JA","JAE","JB","JBE","PUSH","POP","CALL","RET",
        "SYSCALL","NOP","HLT","DB","DW","DUP"
    );

    private static final Set<String> SYSCALLS = Set.of(
        "EXIT","PRINT_CHAR","PRINT_INT","PRINT_STRING",
        "READ_CHAR","READ_INT","READ_STRING","CLEAR_SCREEN",
        "DRAW_PIXEL","DRAW_RECT","DRAW_LINE","READ_PIXEL",
        "FLUSH_SCREEN","SBRK","MALLOC","FREE",
        "ATOI","SLEEP","OPEN_FILE","READ_FILE","WRITE_FILE","CLOSE_FILE"
    );

    private static final Set<String> REGISTERS = Set.of(
        "AX","BX","CX","DX","EX","FX","SP","FP",
        "AL","BL","CL","DL","EL","FL"
    );

    private CharSequence buffer;
    private int start, end, bufferEnd;
    private IElementType tokenType;

    @Override
    public void start(@NotNull CharSequence buf, int startOffset, int endOffset, int initialState) {
        this.buffer = buf;
        this.start = startOffset;
        this.end = startOffset;
        this.bufferEnd = endOffset;
        advance();
    }

    @Override public int getState() {
      return 0;
    }
    @Override public @Nullable IElementType getTokenType() {
      return tokenType;
    }
    @Override public int getTokenStart() {
      return start;
    }
    @Override public int getTokenEnd() {
      return end;
    }
    @Override public @NotNull CharSequence getBufferSequence() {
      return buffer;
    }
    @Override public int getBufferEnd() {
      return bufferEnd;
    }

    @Override
    public void advance() {
        start = end;
        if(start >= bufferEnd) { tokenType = null; return; }

        char c = buffer.charAt(start);

        // Whitespace
        if(Character.isWhitespace(c)) {
            end = start + 1;
            while(end < bufferEnd && Character.isWhitespace(buffer.charAt(end))) {
              end++;
            }
            tokenType = AsmTokenTypes.WHITE_SPACE;
            return;
        }

        // Comment  ;...
        if(c == ';') {
            end = start;
            while(end < bufferEnd && buffer.charAt(end) != '\n') {
              end++;
            }
            tokenType = AsmTokenTypes.COMMENT;
            return;
        }

        // String  "..."
        if(c == '"') {
            end = start + 1;
            while(end < bufferEnd) {
                char ch = buffer.charAt(end++);
                if(ch == '\\') {
                  if(end < bufferEnd) {
                    end++;
                  }
                } else if(ch == '"') {
                  break;
                }
            }
            tokenType = AsmTokenTypes.STRING;
            return;
        }

        // Numbers: 0x hex, 0b binary, decimal
        if(c == '0' && start + 1 < bufferEnd) {
            char next = buffer.charAt(start + 1);
            if(next == 'x' || next == 'X') {
                end = start + 2;
                while(end < bufferEnd && isHexDigit(buffer.charAt(end))) {
                  end++;
                }
                tokenType = AsmTokenTypes.NUMBER_HEX;
                return;
            }
            if(next == 'b' || next == 'B') {
                end = start + 2;
                while(end < bufferEnd && (buffer.charAt(end) == '0' || buffer.charAt(end) == '1')) {
                  end++;
                }
                tokenType = AsmTokenTypes.NUMBER_BINARY;
                return;
            }
        }
        if(Character.isDigit(c)) {
            end = start;
            while(end < bufferEnd && Character.isDigit(buffer.charAt(end))) {
              end++;
            }
            tokenType = AsmTokenTypes.NUMBER;
            return;
        }

        // Identifiers, keywords, registers, syscalls, labels
        if(Character.isLetter(c) || c == '_') {
            end = start;
            while(end < bufferEnd && (Character.isLetterOrDigit(buffer.charAt(end)) || buffer.charAt(end) == '_')) {
              end++;
            }
            String word = buffer.subSequence(start, end).toString();

            // Label: identifier immediately followed by ':'
            if(end < bufferEnd && buffer.charAt(end) == ':') {
                end++; // consume the colon
                tokenType = AsmTokenTypes.LABEL;
                return;
            }

            String upper = word.toUpperCase();
            if(KEYWORDS.contains(upper)){
              tokenType = AsmTokenTypes.KEYWORD;
              return;
            }
            if(SYSCALLS.contains(upper)) {
              tokenType = AsmTokenTypes.SYSCALL;
              return;
            }
            if(REGISTERS.contains(upper)){
              tokenType = AsmTokenTypes.REGISTER;
              return;
            }
            tokenType = AsmTokenTypes.IDENTIFIER;
            return;
        }

        // Operators
        if(c == '+' || c == '-' || c == '*' || c == '/') {
            end = start + 1;
            tokenType = AsmTokenTypes.OPERATOR;
            return;
        }

        // Delimiters
        if(c == ',' || c == '[' || c == ']' || c == '(' || c == ')') {
            end = start + 1;
            tokenType = AsmTokenTypes.DELIMITER;
            return;
        }

        // Fallback
        end = start + 1;
        tokenType = AsmTokenTypes.BAD_CHARACTER;
    }

    private boolean isHexDigit(char ch) {
        return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
    }
}
