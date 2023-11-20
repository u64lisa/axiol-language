package axiol;

import axiol.lexer.Lexer;
import axiol.lexer.TokenType;

import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Lexer lexer = new Lexer();
        lexer.addRule(TokenType.A, lexerRule -> lexerRule.addString("="));
        lexer.addRule(TokenType.B, lexerRule -> lexerRule.addRegexes("[a-zA-Z_][a-zA-Z0-9_]*"));
        lexer.addRule(TokenType.C, lexerRule -> lexerRule.addRegexes("[ \t\r\n]+"));
        lexer.addRule(TokenType.D, lexerRule -> lexerRule.addRegexes("\n"));


        System.out.println(lexer.tokenize(readFile("/test/syntax.ax")));
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