package axiol.parser.expression;

import axiol.lexer.TokenType;

public enum Operator {

    // Basic Assignment
    ASSIGN(        "=",  TokenType.EQUAL,          false, false),

    // Logical Operators
    AND(           "&&", TokenType.EQUAL_EQUAL,    false, false),
    OR(            "||", TokenType.OR_OR,          false, false),

    // Increment and Decrement
    DECREASE(      "--", TokenType.MINUS_MINUS,    true, false), // unary
    INCREASE(      "++", TokenType.PLUS_PLUS,      true, false), // unary

    // Basic Arithmetic
    PLUS(          "+",  TokenType.PLUS,           false, false),
    MINUS(         "-",  TokenType.MINUS,          false, false),
    MULTIPLE(      "*",  TokenType.MULTIPLY,       false, false),
    DIVIDE(        "/",  TokenType.DIVIDE,         false, false),
    MOD(           "%",  TokenType.MOD,            false, false),
    XOR(           "^",  TokenType.XOR,            false, false),
    QUESTION(      "?",  TokenType.QUESTION,       false, false),
    NOT(           "!",  TokenType.QUESTION,       true,  true), // unary

    // Comparison
    LESS_THAN(     "<",  TokenType.LESS_THAN,      false, false),
    MORE_THAN(     ">",  TokenType.MORE_THAN,      false, false),
    EQUAL_EQUAL(   "==", TokenType.EQUAL_EQUAL,    false, false),

    // Comparison with Assignment
    LESS_EQUAL(    "<=", TokenType.LESS_EQUAL,     false, false),
    MORE_EQUAL(    ">=", TokenType.MORE_EQUAL,     false, false),

    // Arithmetic with Assignment
    MIN_EQUAL(     "-=", TokenType.MINUS_EQUAL,    false, false),
    MUL_EQUAL(     "*=", TokenType.MULTIPLY_EQUAL, false, false),
    DIVIDE_EQUAL(  "/=", TokenType.DIVIDE_EQUAL,   false, false),
    XOR_EQUAL(     "^=", TokenType.XOR_EQUAL,      false, false),
    NOR_EQUAL(     "~=", TokenType.NOR_EQUAL,      false, false),
    QUESTION_EQUAL("?=", TokenType.QUESTION_EQUAL, false, false),
    OR_EQUAL(      "|=", TokenType.OR_EQUAL,       false, false),

    // Bitwise and Shift Operators
    BIT_OR(        "|",  TokenType.OR,             false, false),
    SHIFT_LEFT(    "<<", TokenType.SHIFT_LEFT,     false, false),
    SHIFT_RIGHT(   ">>", TokenType.SHIFT_RIGHT,    false, false),
    NOR(           "~",  TokenType.NOR,            true,  false), // unary
    ;

    private final String text;
    private final TokenType type;
    private final boolean unary;
    private final boolean leftAssociated;

    Operator(String text, TokenType type, boolean unary, boolean leftAssociated) {
        this.text = text;
        this.type = type;
        this.unary = unary;
        this.leftAssociated = leftAssociated;
    }

    public String getText() {
        return text;
    }

    public TokenType getType() {
        return type;
    }

    public boolean isLeftAssociated() {
        return leftAssociated;
    }

    public boolean isUnary() {
        return unary;
    }
}
