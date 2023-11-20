package axiol.parser.expression;

import axiol.lexer.TokenType;

public enum Operator {

    //// math dual char
    //LEXER.addRule(TokenType.EQUAL_EQUAL, lexerRule ->    lexerRule.addString("=="));
    //LEXER.addRule(TokenType.EQUAL_NOT, lexerRule ->      lexerRule.addString("!="));
    //LEXER.addRule(TokenType.LESS_EQUAL, lexerRule ->     lexerRule.addString("<="));
    //LEXER.addRule(TokenType.MORE_EQUAL, lexerRule ->     lexerRule.addString(">="));
    //LEXER.addRule(TokenType.PLU_EQUAL, lexerRule ->      lexerRule.addString("+="));
    //LEXER.addRule(TokenType.MIN_EQUAL, lexerRule ->      lexerRule.addString("-="));
    //LEXER.addRule(TokenType.MUL_EQUAL, lexerRule ->      lexerRule.addString("*="));
    //LEXER.addRule(TokenType.DIVIDE_EQUAL, lexerRule ->   lexerRule.addString("/="));
    //LEXER.addRule(TokenType.XOR_EQUAL, lexerRule ->      lexerRule.addString("^="));
    //LEXER.addRule(TokenType.NOR_EQUAL, lexerRule ->      lexerRule.addString("~="));
    //LEXER.addRule(TokenType.QUESTION_EQUAL, lexerRule -> lexerRule.addString("?="));
    //LEXER.addRule(TokenType.OR_EQUAL, lexerRule ->       lexerRule.addString("|=")); // todo remove this is only used as spread sheet
    //LEXER.addRule(TokenType.AND_AND, lexerRule ->        lexerRule.addString("&&"));
    //LEXER.addRule(TokenType.PLUS_PLUS, lexerRule ->      lexerRule.addString("++"));
    //LEXER.addRule(TokenType.MINUS_MINUS, lexerRule ->    lexerRule.addString("--"));
    //LEXER.addRule(TokenType.OR_OR, lexerRule ->          lexerRule.addString("||"));
    //LEXER.addRule(TokenType.SHIFT_LEFT, lexerRule ->     lexerRule.addString("<<"));
    //LEXER.addRule(TokenType.SHIFT_RIGHT, lexerRule ->    lexerRule.addString(">>"));
    //
    //// math basic
    //LEXER.addRule(TokenType.PLUS, lexerRule ->      lexerRule.addString("+"));
    //LEXER.addRule(TokenType.MINUS, lexerRule ->     lexerRule.addString("-"));
    //LEXER.addRule(TokenType.MULTIPLE, lexerRule ->  lexerRule.addString("*"));
    //LEXER.addRule(TokenType.DIVIDE, lexerRule ->    lexerRule.addString("/"));
    //LEXER.addRule(TokenType.AND, lexerRule ->       lexerRule.addString("&"));
    //LEXER.addRule(TokenType.MOD, lexerRule ->       lexerRule.addString("%"));
    //LEXER.addRule(TokenType.OR, lexerRule ->        lexerRule.addString("|"));
    //LEXER.addRule(TokenType.LESS_THAN, lexerRule -> lexerRule.addString("<"));
    //LEXER.addRule(TokenType.MORE_THAN, lexerRule -> lexerRule.addString(">"));
    //LEXER.addRule(TokenType.NOR, lexerRule ->       lexerRule.addString("~"));
    //LEXER.addRule(TokenType.XOR, lexerRule ->       lexerRule.addString("^"));
    //LEXER.addRule(TokenType.EQUAL, lexerRule ->     lexerRule.addString("="));
    //LEXER.addRule(TokenType.QUESTION, lexerRule ->  lexerRule.addString("?"));

    ASSIGN("=", TokenType.EQUAL, false, false),

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
