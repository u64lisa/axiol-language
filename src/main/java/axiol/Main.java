package axiol;

import axiol.lexer.Lexer;

import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Lexer lexer = new Lexer();

        lexer.addTokenRule("NUMBER", "\\d+");
        lexer.addTokenRule("IDENTIFIER", "[a-zA-Z_]+");
        lexer.addTokenRule("PLUS", "\\+");
        lexer.addTokenRule("MINUS", "\\-");
        lexer.addTokenRule("EQUALS", "\\=");
        lexer.addTokenRule("WHITESPACE", "\\s+");

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