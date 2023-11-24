package axiol;

import axiol.lexer.LanguageLexer;
import axiol.lexer.Token;
import axiol.parser.LanguageParser;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        LanguageParser languageParser = new LanguageParser();

        //languageParser.parseSource("/test/syntax.ax", readFile("/test/syntax.ax"));
        languageParser.parseSource("/test/expressions.ax", readFile("/test/expressions.ax"));

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