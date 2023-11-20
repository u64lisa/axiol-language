package axiol;

import axiol.lexer.Lexer;
import axiol.lexer.Token;
import axiol.lexer.TokenType;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    private static final Lexer LEXER = new Lexer();

    static {
        // whitespaces
        LEXER.addRule(TokenType.WHITESPACE, lexerRule -> lexerRule.addRegexes("[ \t\r\n]+"));
        LEXER.addRule(TokenType.WHITESPACE, lexerRule -> lexerRule.addRegexes("//[^\\r\\n]*"));           // comment
        LEXER.addRule(TokenType.WHITESPACE, lexerRule -> lexerRule.addMultiline("/*", "*/")); // comment

        // math dual char
        LEXER.addRule(TokenType.EQUAL_EQUAL, lexerRule ->    lexerRule.addString("=="));
        LEXER.addRule(TokenType.EQUAL_NOT, lexerRule ->      lexerRule.addString("!="));
        LEXER.addRule(TokenType.LESS_EQUAL, lexerRule ->     lexerRule.addString("<="));
        LEXER.addRule(TokenType.MORE_EQUAL, lexerRule ->     lexerRule.addString(">="));
        LEXER.addRule(TokenType.PLU_EQUAL, lexerRule ->      lexerRule.addString("+="));
        LEXER.addRule(TokenType.MIN_EQUAL, lexerRule ->      lexerRule.addString("-="));
        LEXER.addRule(TokenType.MUL_EQUAL, lexerRule ->      lexerRule.addString("*="));
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
        LEXER.addRule(TokenType.MULTIPLE, lexerRule ->  lexerRule.addString("*"));
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

        // literals, types
        LEXER.addRule(TokenType.BOOLEAN, lexerRule -> lexerRule.addString("true", "false"));
        LEXER.addRule(TokenType.CHAR, lexerRule ->    lexerRule.addMultiline("'", "\\", "'"));
        LEXER.addRule(TokenType.STRING, lexerRule ->  lexerRule.addMultiline("\"", "\\", "\""));
        LEXER.addRule(TokenType.LITERAL, lexerRule -> lexerRule.addRegexes("[a-zA-Z_][a-zA-Z0-9_]*"));

        // numbers
        LEXER.addRule(TokenType.FLOAT, lexerRule -> lexerRule.addRegexes("[0-9]+(\\.[0-9]+)?[fF]"));
        LEXER.addRule(TokenType.LONG, lexerRule -> lexerRule.addRegexes("[0-9]+[Ll]"));
        LEXER.addRule(TokenType.INT, lexerRule -> lexerRule.addRegexes("[0-9]+"));
        LEXER.addRule(TokenType.DOUBLE, lexerRule -> lexerRule.addRegexes("[0-9]+(\\.[0-9]+)?[dD]?"));

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

        // keywords

    }

    public static void main(String[] args) {
        List<Token> tokens = LEXER.tokenize(readFile("/test/syntax.ax"), true);
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    public static String readFile(String file) {
        Scanner scanner = new Scanner(Objects.requireNonNull(Main.class.getResourceAsStream(file)));
        StringBuilder fileContents = new StringBuilder();
        while (scanner.hasNextLine()) {
            fileContents.append(scanner.nextLine()).append("\n");
        }
        scanner.close();
        return fileContents.toString();
    }
}