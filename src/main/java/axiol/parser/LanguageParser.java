package axiol.parser;

import axiol.lexer.LanguageLexer;
import axiol.parser.stream.TokenStream;
import axiol.parser.tree.TreeRootNode;
import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;

import java.io.File;
import java.util.Scanner;

public class LanguageParser extends Parser {

    private TokenStream tokenStream;
    private String source;

    @Override
    public TreeRootNode parseFile(File file) throws Throwable {
        StringBuilder builder = new StringBuilder();
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine())
            builder.append(scanner.nextLine()).append("\n");

        scanner.close();
        return parseSource(builder.toString());
    }

    @Override
    public TreeRootNode parseSource(String content) {
        TreeRootNode treeRootNode = new TreeRootNode();
        LanguageLexer lexer = new LanguageLexer();

        this.tokenStream = new TokenStream(lexer.tokenizeString(content));
        this.source = content;

        while (tokenStream.hasMoreTokens()) {
            Statement statement = this.parseStatement();

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
}
