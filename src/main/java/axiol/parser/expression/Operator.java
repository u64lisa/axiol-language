package axiol.parser.expression;

import axiol.lexer.TokenType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public enum Operator {

    // Basic Assignment
    ASSIGN(        "=",  TokenType.EQUAL,          1,  false, false),

    // Logical Operators
    AND(           "&&", TokenType.EQUAL_EQUAL,    3,  false, false),
    OR(            "||", TokenType.OR_OR,          2,  false, false),

    // Increment and Decrement
    DECREASE(      "--", TokenType.MINUS_MINUS,    11, true, false), // unary
    INCREASE(      "++", TokenType.PLUS_PLUS,      11, true, false), // unary

    // Basic Arithmetic
    PLUS(          "+",  TokenType.PLUS,           8,  false, false),
    MINUS(         "-",  TokenType.MINUS,          8,  false, false),
    MULTIPLE(      "*",  TokenType.MULTIPLY,       9,  false, false),
    DIVIDE(        "/",  TokenType.DIVIDE,         9,  false, false),
    MOD(           "%",  TokenType.MOD,            9,  false, false),
    XOR(           "^",  TokenType.XOR,            7,  false, false),
    NOT(           "!",  TokenType.NOT,            10, true,  true), // unary

    // Comparison
    LESS_THAN(     "<",  TokenType.LESS_THAN,      4,  false, false),
    MORE_THAN(     ">",  TokenType.MORE_THAN,      4,  false, false),
    EQUAL_EQUAL(   "==", TokenType.EQUAL_EQUAL,    5,  false, false),

    // Comparison with Assignment
    LESS_EQUAL(    "<=", TokenType.LESS_EQUAL,     4,  false, false),
    MORE_EQUAL(    ">=", TokenType.MORE_EQUAL,     4,  false, false),

    // Arithmetic with Assignment
    MIN_EQUAL(     "-=", TokenType.MINUS_EQUAL,    12, false, false),
    MUL_EQUAL(     "*=", TokenType.MULTIPLY_EQUAL, 12, false, false),
    DIVIDE_EQUAL(  "/=", TokenType.DIVIDE_EQUAL,   12, false, false),
    XOR_EQUAL(     "^=", TokenType.XOR_EQUAL,      12, false, false),
    NOR_EQUAL(     "~=", TokenType.NOR_EQUAL,      12, false, false),
    QUESTION_EQUAL("?=", TokenType.QUESTION_EQUAL, 12, false, false),
    OR_EQUAL(      "|=", TokenType.OR_EQUAL,       12, false, false),

    // Bitwise and Shift Operators
    BIT_OR(        "|",  TokenType.OR,             6,  false, false),
    SHIFT_LEFT(    "<<", TokenType.SHIFT_LEFT,     13, false, false),
    SHIFT_RIGHT(   ">>", TokenType.SHIFT_RIGHT,    13, false, false),
    NOR(           "~",  TokenType.NOR,            7,  true,  false), // unary
    ;

    private static final Map<Integer, List<Operator>> SORTED_PRIORITY = new ConcurrentHashMap<>();

    public static final Operator[] VALUES = Operator.values();
    public static final int MAX_PRIORITY = 13;
    public static final int MIN_PRIORITY = 1;

    private final String text;
    private final TokenType type;
    private final int priority;
    private final boolean unary;
    private final boolean leftAssociated;

    Operator(String text, TokenType type, int priority, boolean unary, boolean leftAssociated) {
        this.text = text;
        this.type = type;
        this.priority = priority;
        this.unary = unary;
        this.leftAssociated = leftAssociated;
    }

    public static List<Operator> getOperatorsByPriority(int priority) {
        if (SORTED_PRIORITY.containsKey(priority))
            return SORTED_PRIORITY.get(priority);

        List<Operator> operators = new ArrayList<>();
        for (Operator value : VALUES) {
            if (value.priority == priority)
                operators.add(value);
        }

        SORTED_PRIORITY.put(priority, operators);

        return operators;
    }

    public String getText() {
        return text;
    }

    public TokenType getType() {
        return type;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isLeftAssociated() {
        return leftAssociated;
    }

    public boolean isUnary() {
        return unary;
    }
}
