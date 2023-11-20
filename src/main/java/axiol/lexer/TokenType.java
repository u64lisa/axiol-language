package axiol.lexer;

public enum TokenType {

    // skipped empty's
    WHITESPACE,

    LITERAL,

    PLUS,
    MINUS,
    MULTIPLE,
    DIVIDE,
    AND,
    MOD,
    OR,
    LESS_THAN,
    MORE_THAN,
    NOR,
    EQUAL,
    XOR,
    EQUAL_EQUAL,
    EQUAL_NOT,
    MORE_EQUAL,
    OR_EQUAL,
    XOR_EQUAL,
    DIVIDE_EQUAL,
    QUESTION_EQUAL,
    SHIFT_LEFT,
    FN_ACCESS,
    OR_OR,
    PLUS_PLUS,
    AND_AND,
    MINUS_MINUS,
    SHIFT_RIGHT,
    MUL_EQUAL,
    MIN_EQUAL,
    LESS_EQUAL,
    PLU_EQUAL,
    NOR_EQUAL,
    QUESTION,

    LAMBDA,

    BOOLEAN,
    CHAR,
    STRING,

    L_CURLY,
    R_CURLY,
    L_PAREN,
    R_PAREN,
    L_SQUARE,
    R_SQUARE,

    SEMICOLON,
    VARARGS,
    DOT,
    COMMA,
    AT,
    UNDERSCORE,
    COLON,

    FLOAT,
    LONG,
    INT,
    DOUBLE,
    HEX_NUM,

    ELSE,
    WHILE,
    FOR,
    IF,
    MATCH,
    LOOP,
    CASE,
    BREAK,
    RETURN,
    CONTINUE,
    UNREACHABLE,
    SWITCH,
    DEFAULT,
    PRIVATE,
    PUBLIC,
    PROTECTED,
    INLINE,
    CONST,
    EXTERN,
    ASM,
    FUNCTION,
    CLASS,
    STRUCTURE,
    CONSTRUCT,
    PARENT,
    USING,
    OVERRIDE,
    LINKED,
    INSET,
    UNSAFE,

    EOF,

    ;

    public boolean isNumber() {
        TokenType type = this;
        return switch (type) {
            case INT, DOUBLE, FLOAT, LONG -> true;
            default -> false;
        };
    }

}
