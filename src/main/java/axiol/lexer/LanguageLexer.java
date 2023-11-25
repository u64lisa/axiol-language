package axiol.lexer;

import axiol.parser.util.error.Position;

import java.util.List;

public class LanguageLexer {

    private static final Lexer LEXER = new Lexer();

    static {
        // whitespaces
        LEXER.addRule(TokenType.WHITESPACE, lexerRule -> lexerRule.addRegexes("[ \t\r\n]+"));
        LEXER.addRule(TokenType.WHITESPACE, lexerRule -> lexerRule.addRegexes("//[^\\r\\n]*"));           // comment
        LEXER.addRule(TokenType.WHITESPACE, lexerRule -> lexerRule.addMultiline("/*", "*/")); // comment

        // lambda
        LEXER.addRule(TokenType.LAMBDA, lexerRule ->      lexerRule.addString("->"));
        LEXER.addRule(TokenType.REV_LAMBDA, lexerRule ->      lexerRule.addString("<-"));

        // math dual char
        LEXER.addRule(TokenType.EQUAL_EQUAL, lexerRule ->    lexerRule.addString("=="));
        LEXER.addRule(TokenType.EQUAL_NOT, lexerRule ->      lexerRule.addString("!="));
        LEXER.addRule(TokenType.LESS_EQUAL, lexerRule ->     lexerRule.addString("<="));
        LEXER.addRule(TokenType.MORE_EQUAL, lexerRule ->     lexerRule.addString(">="));
        LEXER.addRule(TokenType.PLUS_EQUAL, lexerRule ->      lexerRule.addString("+="));
        LEXER.addRule(TokenType.MINUS_EQUAL, lexerRule ->      lexerRule.addString("-="));
        LEXER.addRule(TokenType.MULTIPLY_EQUAL, lexerRule ->      lexerRule.addString("*="));
        LEXER.addRule(TokenType.DIVIDE_EQUAL, lexerRule ->   lexerRule.addString("/="));
        LEXER.addRule(TokenType.XOR_EQUAL, lexerRule ->      lexerRule.addString("^="));
        LEXER.addRule(TokenType.NOR_EQUAL, lexerRule ->      lexerRule.addString("~="));
        LEXER.addRule(TokenType.QUESTION_EQUAL, lexerRule -> lexerRule.addString("?="));
        LEXER.addRule(TokenType.OR_EQUAL, lexerRule ->       lexerRule.addString("|="));
        LEXER.addRule(TokenType.AND_AND, lexerRule ->        lexerRule.addString("&&"));
        LEXER.addRule(TokenType.PLUS_PLUS, lexerRule ->      lexerRule.addString("++"));
        LEXER.addRule(TokenType.MINUS_MINUS, lexerRule ->    lexerRule.addString("--"));
        LEXER.addRule(TokenType.OR_OR, lexerRule ->          lexerRule.addString("||"));
        LEXER.addRule(TokenType.SHIFT_LEFT, lexerRule ->     lexerRule.addString("<<"));
        LEXER.addRule(TokenType.SHIFT_RIGHT, lexerRule ->    lexerRule.addString(">>"));
        LEXER.addRule(TokenType.FN_ACCESS, lexerRule ->      lexerRule.addString("::"));

        // math basic
        LEXER.addRule(TokenType.PLUS, lexerRule ->      lexerRule.addString("+"));
        LEXER.addRule(TokenType.MINUS, lexerRule ->     lexerRule.addString("-"));
        LEXER.addRule(TokenType.MULTIPLY, lexerRule ->  lexerRule.addString("*"));
        LEXER.addRule(TokenType.DIVIDE, lexerRule ->    lexerRule.addString("/"));
        LEXER.addRule(TokenType.AND, lexerRule ->       lexerRule.addString("&"));
        LEXER.addRule(TokenType.MOD, lexerRule ->       lexerRule.addString("%"));
        LEXER.addRule(TokenType.OR, lexerRule ->        lexerRule.addString("|"));
        LEXER.addRule(TokenType.LESS_THAN, lexerRule -> lexerRule.addString("<"));
        LEXER.addRule(TokenType.MORE_THAN, lexerRule -> lexerRule.addString(">"));
        LEXER.addRule(TokenType.NOR, lexerRule ->       lexerRule.addString("~"));
        LEXER.addRule(TokenType.XOR, lexerRule ->       lexerRule.addString("^"));
        LEXER.addRule(TokenType.EQUAL, lexerRule ->     lexerRule.addString("="));
        LEXER.addRule(TokenType.QUESTION, lexerRule ->  lexerRule.addString("?"));
        LEXER.addRule(TokenType.NOT, lexerRule ->  lexerRule.addString("!"));

        // types
        LEXER.addRule(TokenType.BOOLEAN, lexerRule -> lexerRule.addString("true", "false"));
        LEXER.addRule(TokenType.CHAR, lexerRule ->    lexerRule.addMultiline("'", "\\", "'"));
        LEXER.addRule(TokenType.STRING, lexerRule ->  lexerRule.addMultiline("\"", "\\", "\""));

        // numbers
        LEXER.addRule(TokenType.FLOAT, lexerRule ->   lexerRule.addRegexes("[0-9]+(\\.[0-9]+)?[fF][uU]?"));
        LEXER.addRule(TokenType.LONG, lexerRule ->    lexerRule.addRegexes("[0-9]+[Ll][uU]?"));
        LEXER.addRule(TokenType.INT, lexerRule ->     lexerRule.addRegexes("[0-9]+?[uU][uU]?"));
        LEXER.addRule(TokenType.DOUBLE, lexerRule ->  lexerRule.addRegexes("[0-9]+(\\.[0-9]+)?[dD]?[uU]?"));
        LEXER.addRule(TokenType.HEX_NUM, lexerRule -> lexerRule.addRegexes("0x[0-9a-fA-F]+"));

        // brackets
        LEXER.addRule(TokenType.L_CURLY, lexerRule ->   lexerRule.addString("{"));
        LEXER.addRule(TokenType.R_CURLY, lexerRule ->   lexerRule.addString("}"));
        LEXER.addRule(TokenType.L_PAREN, lexerRule ->   lexerRule.addString("("));
        LEXER.addRule(TokenType.R_PAREN, lexerRule ->   lexerRule.addString(")"));
        LEXER.addRule(TokenType.L_SQUARE, lexerRule ->  lexerRule.addString("["));
        LEXER.addRule(TokenType.R_SQUARE, lexerRule ->  lexerRule.addString("]"));

        // dot, coma, others
        LEXER.addRule(TokenType.SEMICOLON, lexerRule ->  lexerRule.addString(";"));
        LEXER.addRule(TokenType.VARARGS, lexerRule ->    lexerRule.addString("..."));
        LEXER.addRule(TokenType.DOT, lexerRule ->        lexerRule.addString("."));
        LEXER.addRule(TokenType.COMMA, lexerRule ->      lexerRule.addString(","));
        LEXER.addRule(TokenType.AT, lexerRule ->         lexerRule.addString("@"));
        LEXER.addRule(TokenType.UNDERSCORE, lexerRule -> lexerRule.addString("_"));
        LEXER.addRule(TokenType.COLON, lexerRule ->      lexerRule.addString(":"));

        // keywords control-flow
        LEXER.addRule(TokenType.IF, lexerRule ->          lexerRule.addString("if"));
        LEXER.addRule(TokenType.ELSE, lexerRule ->        lexerRule.addString("else"));
        LEXER.addRule(TokenType.FOR, lexerRule ->         lexerRule.addString("for"));
        LEXER.addRule(TokenType.WHILE, lexerRule ->       lexerRule.addString("while"));
        LEXER.addRule(TokenType.DO, lexerRule ->          lexerRule.addString("do"));
        LEXER.addRule(TokenType.MATCH, lexerRule ->       lexerRule.addString("match"));
        LEXER.addRule(TokenType.SWITCH, lexerRule ->      lexerRule.addString("switch"));
        LEXER.addRule(TokenType.DEFAULT, lexerRule ->     lexerRule.addString("default"));
        LEXER.addRule(TokenType.LOOP, lexerRule ->        lexerRule.addString("loop"));
        LEXER.addRule(TokenType.CASE, lexerRule ->        lexerRule.addString("case"));
        LEXER.addRule(TokenType.BREAK, lexerRule ->       lexerRule.addString("break"));
        LEXER.addRule(TokenType.RETURN, lexerRule ->      lexerRule.addString("return"));
        LEXER.addRule(TokenType.YIELD, lexerRule ->       lexerRule.addString("yield"));
        LEXER.addRule(TokenType.CONTINUE, lexerRule ->    lexerRule.addString("continue"));
        LEXER.addRule(TokenType.UNREACHABLE, lexerRule -> lexerRule.addString("unreachable"));

        // access modifier
        LEXER.addRule(TokenType.PUBLIC, lexerRule ->     lexerRule.addString("public"));
        LEXER.addRule(TokenType.PRIVATE, lexerRule ->    lexerRule.addString("private"));
        LEXER.addRule(TokenType.PROTECTED, lexerRule ->  lexerRule.addString("protected"));
        LEXER.addRule(TokenType.INLINE, lexerRule ->     lexerRule.addString("inline"));
        LEXER.addRule(TokenType.CONST, lexerRule ->      lexerRule.addString("const"));
        LEXER.addRule(TokenType.EXTERN, lexerRule ->     lexerRule.addString("extern"));

        // custom
        LEXER.addRule(TokenType.ASM, lexerRule ->        lexerRule.addString("asm"));
        LEXER.addRule(TokenType.LINKED, lexerRule ->     lexerRule.addString("linked"));
        LEXER.addRule(TokenType.INSET, lexerRule ->      lexerRule.addString("inset"));  // keyword to edits IN-struction-SET
        LEXER.addRule(TokenType.UNSAFE, lexerRule ->      lexerRule.addString("unsafe"));

        // classes, functions
        LEXER.addRule(TokenType.FUNCTION, lexerRule ->   lexerRule.addString("function"));
        LEXER.addRule(TokenType.CLASS, lexerRule ->      lexerRule.addString("class"));
        LEXER.addRule(TokenType.PARENT, lexerRule ->     lexerRule.addString("parent"));
        LEXER.addRule(TokenType.CONSTRUCT, lexerRule ->  lexerRule.addString("construct"));
        LEXER.addRule(TokenType.STRUCTURE, lexerRule ->  lexerRule.addString("structure"));
        LEXER.addRule(TokenType.USING, lexerRule ->      lexerRule.addString("using"));
        LEXER.addRule(TokenType.OVERRIDE, lexerRule ->   lexerRule.addString("override"));

        // literals ! last element or else many things are broken
        LEXER.addRule(TokenType.LITERAL, lexerRule -> lexerRule.addRegexes("[a-zA-Z_][a-zA-Z0-9_]*"));
    }

    public List<Token> tokenizeString(final String content) {
        List<Token> tokens = LEXER.tokenize(content, true);

        tokens.add(new Token(TokenType.EOF, "EOF", new Position(-1, -1)));

        return tokens;
    }

}
