.MEMORY 8K

; directives: no issues
hello_str:  DB "Hello, World!", 10, 0
wide_val:   DW 0x1234
arr:        DB 256 DUP(0)
char_val:   DB 'A', '\n', 0

; lexer, strings and character literals

; strings
ok_str:     DB "closed", 0
ok_char:    DB 'Z', 0

; hex and binary numbers: should highlight as numbers
nums:       DW 0xFF, 0b1010, 255

; label folding

; this should produce
; a
; fold
; region

main:
; label should fold from here to next label

    MOV AX, 0
    MOV BX, 1
    MOV CX, 10

; goto declaration and find usages

loop_top:
    ADD AX, BX
    CMP AX, 100
    JNE loop_top
    JE loop_top

    JMP done

; code completion: correct param suggestions

    MOV AX, 42
    MOV BX, AX
    MOV AL, 0xFF
    ADD AX, BX
    ADD AX, 5
    SUB BX, AX
    MUL CX
    DIV DX
    PUSH AX
    POP BX
    SYSCALL PRINT_INT
    SYSCALL PRINT_INT

; operand validation

; these should be valid
    MOV CX, 0
    INC CX
    DEC CX
    NOT AX
    SHL AX, 2
    SHR AX, 1
    AND AX, 0xFF
    OR  AX, BX
    XOR AX, AX
    CMP AX, 0
    LEA BX, [FP+4]
    RET

; these should be invalid

; too many operands, CX should be underlined red
    MOV AX, BX, CX

; not enough operands: ADD should be underlined red
    ADD AX

; not enough operands: MUL should be underlined red
    MUL

; wrong type for WORD_REG, AL should be underlined yellow
    MUL AL

; wrong type for LABEL, AX should be underlined yellow
    JMP AX

; wrong type for SYSCALL, AX should be underlined yellow
    SYSCALL AX


; these should be valid
store_section:
    MOV AX, 1
    MOV [BX], AX
    MOV AX, [BX]
    MOV [BX+2], AX
    MOV AX, [BX+CX]
    INC [BX]
    DEC [FP+2]
    ADD AX, [BX]
    CMP AX, [FP+4]

; unresolved label, should show yellow warning
    JMP nonexistent

; inline labels
; should be valid
entry:      MOV AX, 0
exit_pt:    RET

; indentation

main2:
    MOV AX, 0
    RET

; syscall should shouldn't be marked as identifier here

    SYSCALL EXIT
    SYSCALL PRINT_CHAR
    SYSCALL PRINT_STRING
    SYSCALL PRINT_INT
    SYSCALL READ_INT
    SYSCALL MALLOC
    SYSCALL FREE

done:
    SYSCALL EXIT