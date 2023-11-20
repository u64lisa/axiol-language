package axiol;

import axiol.lexer.Lexer;

public class Main {
    public static void main(String[] args) {

        Lexer lexer = new Lexer();

        lexer.addTokenRule("NUMBER", "\\d+");
        lexer.addTokenRule("IDENTIFIER", "[a-zA-Z_]+");
        lexer.addTokenRule("PLUS", "\\+");
        lexer.addTokenRule("PLUS", "\\-");
        lexer.addTokenRule("WHITESPACE", "\\s+");

        System.out.println(lexer.tokenize("test + 12 - OwO"));

    }
}