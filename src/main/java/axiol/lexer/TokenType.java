package axiol.lexer;

public enum TokenType {

    // skipped empty's
    WHITESPACE,

    LITERAL,

    PLUS,
    MINUS,
    MULTIPLY,
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
    MULTIPLY_EQUAL,
    MINUS_EQUAL,
    LESS_EQUAL,
    PLUS_EQUAL,
    NOR_EQUAL,
    QUESTION,
    NOT,

    LAMBDA,
    REV_LAMBDA,

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

    BYTE,
    SHORT,
    FLOAT,
    LONG,
    INT,
    DOUBLE,
    BIG_NUMBER,
    HEX_NUM,
    BIG_HEX_NUM,

    ELSE,
    WHILE,
    DO,
    FOR,
    IF,
    MATCH,
    LOOP,
    CASE,
    BREAK,
    RETURN,
    YIELD,
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
    NATIVE,
    ASM,
    FUNCTION,
    CLASS,
    STRUCTURE,
    CONSTRUCT,
    PARENT,
    USING,
    OVERRIDE,
    LINKED,
    ENUM,
    ISA,
    UNSAFE,
    CAST,
    STACK_ALLOC,

    EOF,

    ;

}
