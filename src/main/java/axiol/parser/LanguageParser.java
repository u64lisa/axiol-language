package axiol.parser;

import axiol.lexer.LanguageLexer;
import axiol.lexer.Token;
import axiol.lexer.TokenType;
import axiol.parser.expression.Operator;
import axiol.parser.tree.statements.VariableStatement;
import axiol.parser.util.Parser;
import axiol.parser.util.error.ParseException;
import axiol.parser.util.error.Position;
import axiol.parser.util.stream.TokenStream;
import axiol.parser.tree.TreeRootNode;
import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;
import axiol.types.ParsedType;
import axiol.types.PrimitiveTypes;
import axiol.types.Type;
import axiol.types.TypeCollection;

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
        if (isVariable())
            return this.parseVariableStatement();

        this.createSyntaxError("not statement parsable from token '%s'", this.tokenStream.current());
        return null;
    }

    public Statement parseVariableStatement() {
        ParsedType type = this.parseType();

        // todo add access modifier

        boolean pointer = false;
        if (this.tokenStream.matches(TokenType.MULTIPLY)) {
            pointer = true;
            this.tokenStream.advance();
        }

        if (!expected(TokenType.LITERAL))
            return null;

        String name = this.tokenStream.current().getValue();
        this.tokenStream.advance();

        if (!expected(TokenType.EQUAL))
            return null;

        this.tokenStream.advance();

        Expression expression = this.parseExpression();

        if (!expected(TokenType.SEMICOLON))
            return null;
        this.tokenStream.advance();

        return new VariableStatement(name, type, expression, pointer);
    }

    public boolean isVariable() {
        return !TypeCollection.typeByToken(this.tokenStream.current()).equals(TypeCollection.NONE);
    }

    public ParsedType parseType() {
        Token current = this.tokenStream.current();

        Type type = TypeCollection.typeByToken(current);
        if (type == TypeCollection.NONE) {
            String value = current.getValue();
            this.tokenStream.advance();

            // todo classes structs and other
            return null;
        }
        this.tokenStream.advance();

        int arrayDepth = 0;
        while (this.tokenStream.matches(TokenType.L_SQUARE)) {
            this.tokenStream.advance();

            this.expected(TokenType.R_SQUARE);
            this.tokenStream.advance();
            arrayDepth++;
        }

        return new ParsedType(type, arrayDepth);
    }

    @Override
    public Expression parseExpression() {
        return this.expressionParser.parseExpression(Operator.MAX_PRIORITY);
    }

    public boolean expected(TokenType type) {
        if (this.tokenStream.matches(type)) {
            return true;
        }
        createSyntaxError("unexpected token expected '%s' but got '%s'", type, this.tokenStream.current().getType());
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
