package axiol;

import axiol.lexer.LanguageLexer;
import axiol.lexer.Token;
import axiol.parser.LanguageParser;

import java.util.*;

public class Main {

    public static void main(String[] args) {
       // LanguageParser languageParser = new LanguageParser();
       //
       // languageParser.parseSource("/test/expressions.ax", readFile("/test/expressions.ax"));

        LanguageLexer lexer = new LanguageLexer();
        for (Token token : lexer.tokenizeString(readFile("/test/tokens.ax"))) {
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