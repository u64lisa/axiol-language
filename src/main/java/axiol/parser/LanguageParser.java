package axiol.parser;

import axiol.lexer.LanguageLexer;
import axiol.lexer.Token;
import axiol.lexer.TokenType;
import axiol.parser.expression.Operator;
import axiol.parser.util.Parser;
import axiol.parser.util.error.ParseException;
import axiol.parser.util.error.Position;
import axiol.parser.util.stream.TokenStream;
import axiol.parser.tree.TreeRootNode;
import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;
import axiol.types.PrimitiveTypes;

import java.io.File;
import java.util.Scanner;

public class LanguageParser extends Parser {

    private ExpressionParser expressionParser;
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

        this.expressionParser = new ExpressionParser(this);

        while (tokenStream.hasMoreTokens()) {
            Statement statement = this.parseStatement();

            if (statement != null)
                treeRootNode.getStatements().add(statement);
        }

        return treeRootNode;
    }

    @Override
    public Statement parseStatement() {

        this.parseType();

        return null;
    }

    public void parseType() {
        Token current = this.tokenStream.current();

        PrimitiveTypes primitiveTypes = PrimitiveTypes.fromToken(current);
        if (primitiveTypes == null) {
            // todo classes structs and other
            return;
        }
        this.tokenStream.advance();

        int arrayDepth = 0;
        while (this.tokenStream.matches(TokenType.L_SQUARE)) {
            this.tokenStream.advance();

            this.expected(TokenType.R_SQUARE);
            this.tokenStream.advance();
            arrayDepth++;
        }

        System.out.println(primitiveTypes + " " + arrayDepth);

    }

    @Override
    public Expression parseExpression() {
        return this.expressionParser.parseExpression(Operator.MAX_PRIORITY);
    }

    public boolean expected(TokenType type) {
        if (this.tokenStream.matches(type)) {
            return true;
        }
        createSyntaxError("unexpected token expected '%s' but got '%s'", type, this.tokenStream.current());
        return false;
    }

    public boolean expected(String value) {
        if (this.tokenStream.matchesValue(value)) {
            return true;
        }
        createSyntaxError("unexpected token-value expected '%s' but got '%s'", value, this.tokenStream.current().getValue());
        return false;
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

    public TokenStream getTokenStream() {
        return tokenStream;
    }
}
