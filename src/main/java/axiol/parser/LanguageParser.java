package axiol.parser;

import axiol.lexer.LanguageLexer;
import axiol.lexer.Token;
import axiol.parser.error.ParseException;
import axiol.parser.error.Position;
import axiol.parser.stream.TokenStream;
import axiol.parser.tree.TreeRootNode;
import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;

import java.io.File;
import java.util.Scanner;

public class LanguageParser extends Parser {

    private TokenStream tokenStream;
    private String source;
    private String path;

    @Override
    public TreeRootNode parseFile(File file) throws Throwable {
        StringBuilder builder = new StringBuilder();
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine())
            builder.append(scanner.nextLine()).append("\n");

        scanner.close();
        return parseSource(file.toPath().toString(), builder.toString());
    }

    @Override
    public TreeRootNode parseSource(String path, String content) {
        TreeRootNode treeRootNode = new TreeRootNode();
        LanguageLexer lexer = new LanguageLexer();

        this.tokenStream = new TokenStream(lexer.tokenizeString(content));
        this.source = content;
        this.path = path;

        while (tokenStream.hasMoreTokens()) {
            Statement statement = this.parseStatement();

            System.out.println(tokenStream.current());

            createSyntaxError("test");

            if (statement != null)
                treeRootNode.getStatements().add(statement);
        }

        return treeRootNode;
    }

    @Override
    public Statement parseStatement() {
        return null;
    }

    @Override
    public Expression parseExpression() {
        return null;
    }

    public void createSyntaxError(String message, Object... args) {
        ParseException parseException = new ParseException(source, this.tokenStream.current(), path, message, args);
        parseException.throwError();
    }

    public void createSyntaxError(Token position, String message, Object... args) {
        ParseException parseException = new ParseException(source, position, path, message, args);
        parseException.throwError();
    }

    public void createSyntaxError(Position start, Position end, String message, Object... args) {
        ParseException parseException = new ParseException(source, start, end, path, message, args);
        parseException.throwError();
    }
}
