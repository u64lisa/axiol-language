package axiol.parser;

import axiol.lexer.LanguageLexer;
import axiol.lexer.Token;
import axiol.lexer.TokenType;
import axiol.parser.expression.Operator;
import axiol.parser.statement.Accessibility;
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
import java.util.Arrays;
import java.util.Scanner;

public class LanguageParser extends Parser {

    private final TokenType[] accessModifier = {
            TokenType.PUBLIC, TokenType.PRIVATE, TokenType.INLINE, TokenType.CONST,
            TokenType.EXTERN, TokenType.PROTECTED
    };

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
        if ((isAccessModifier() && isVariable(this.tokenStream.peak(1)))
                || isVariable(this.tokenStream.current())) {
            if (isAccessModifier()) {
                return this.parseVariableStatement(this.parseAccess());
            }
            return this.parseVariableStatement();
        }

        this.createSyntaxError("not statement parsable from token '%s'", this.tokenStream.current());
        return null;
    }

    public Statement parseVariableStatement(Accessibility... accessibility) {
        ParsedType type = this.parseType();

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

        return new VariableStatement(name, type, expression, pointer, accessibility);
    }

    public Accessibility parseAccess() {
        Accessibility accessibility = switch (this.tokenStream.current().getType()) {
            case PUBLIC -> Accessibility.PUBLIC;
            case PRIVATE -> Accessibility.PRIVATE;
            case PROTECTED -> Accessibility.PROTECTED;
            case CONST -> Accessibility.CONST;
            case INLINE -> Accessibility.INLINE;
            case EXTERN -> Accessibility.EXTERN;

            // cover all tokens by default
            default -> {
                createSyntaxError(
                        "expected access modifier but got '%s'",
                        this.tokenStream.current().getValue());
                yield Accessibility.PRIVATE;
            }
        };
        this.tokenStream.advance();

        return accessibility;
    }

    public boolean isVariable(Token token) {
        return !TypeCollection.typeByToken(token).equals(TypeCollection.NONE);
    }

    public boolean isAccessModifier() {
        return Arrays.stream(this.accessModifier).anyMatch(type -> type == this.tokenStream.current().getType());
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
