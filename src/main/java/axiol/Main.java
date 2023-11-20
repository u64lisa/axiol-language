package axiol;

import axiol.lexer.Lexer;
import axiol.lexer.Token;
import axiol.lexer.TokenType;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Lexer lexer = new Lexer();
        lexer.addRule(TokenType.LITERAL, lexerRule -> lexerRule.addRegexes("[a-zA-Z_][a-zA-Z0-9_]*"));

        lexer.addRule(TokenType.WHITESPACE, lexerRule -> lexerRule.addRegexes("[ \t\r\n]+"));

        List<Token> tokens = lexer.tokenize(readFile("/test/syntax.ax"));
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