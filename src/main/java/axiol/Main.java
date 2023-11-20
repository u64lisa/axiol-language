package axiol;

import axiol.lexer.LanguageLexer;
import axiol.lexer.Token;
import axiol.lexer.TokenType;
import axiol.parser.error.ParseException;
import axiol.parser.error.Position;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        LanguageLexer lexer = new LanguageLexer();

        List<Token> tokens = lexer.tokenizeString(readFile("/test/tokens.ax"));
        for (Token token : tokens) {
            System.out.println(token);
        }

        ParseException parseException = new ParseException("TEST TEST TEST\nTEST TEST TEST", new Token(TokenType.STRING, "TEST", new Position(1, 5)), "error", "owo");
        parseException.throwError();
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