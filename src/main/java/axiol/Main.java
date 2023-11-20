package axiol;

import axiol.lexer.Lexer;
import axiol.lexer.TokenType;

import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Lexer lexer = new Lexer();
        lexer.addRule(TokenType.NEW_LINE, lexerRule -> lexerRule.addRegexes("\n"));

        System.out.println(lexer.tokenize(readFile("/test/syntax.ax")));
    }

    public static final String readFile(String file) {
        Scanner scanner = new Scanner(Objects.requireNonNull(Main.class.getResourceAsStream(file)));
        StringBuilder fileContents = new StringBuilder();
        while (scanner.hasNextLine()) {
            fileContents.append(scanner.nextLine()).append("\n");
        }
        scanner.close();
        return fileContents.toString();
    }
}