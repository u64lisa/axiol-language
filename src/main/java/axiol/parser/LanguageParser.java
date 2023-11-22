package axiol.parser;

import axiol.lexer.LanguageLexer;
import axiol.lexer.Token;
import axiol.lexer.TokenType;
import axiol.parser.expression.Operator;
import axiol.parser.statement.Accessibility;
import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;
import axiol.parser.tree.TreeRootNode;
import axiol.parser.tree.statements.BodyStatement;
import axiol.parser.tree.statements.LinkedNoticeStatement;
import axiol.parser.tree.statements.VariableStatement;
import axiol.parser.tree.statements.control.*;
import axiol.parser.util.Parser;
import axiol.parser.util.error.ParseException;
import axiol.parser.util.error.Position;
import axiol.parser.util.stream.TokenStream;
import axiol.types.ParsedType;
import axiol.types.Type;
import axiol.types.TypeCollection;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * The type Language parser.
 */
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

    /**
     * Parse body statements for global scope.
     * contains:
     * - functions
     * - structures
     * - class
     * x global var
     * x import
     * - attributes
     * - constructor
     *
     * @return the statement parsed
     */
    public Statement parseStatement() {
        if ((isAccessModifier() && isType(this.tokenStream.peak(1)))
                || isType(this.tokenStream.current())) {
            if (isAccessModifier()) {
                return this.parseVariableStatement(this.parseAccess());
            }
            return this.parseVariableStatement();
        }

        if ((isAccessModifier() && this.tokenStream.peak(1).getType().equals(TokenType.FUNCTION))
                || this.tokenStream.current().getType().equals(TokenType.FUNCTION)) {
            if (isAccessModifier()) {
                return this.parseFunction(this.parseAccess());
            }
            return this.parseFunction();
        }
        if (this.tokenStream.current().getType().equals(TokenType.LINKED)) {
            this.tokenStream.advance();

            return this.parseLinkingNotice();
        }

        this.createSyntaxError("not statement parsable from token '%s'", this.tokenStream.current());
        return null;
    }

    public BodyStatement parseBodyStatement() {
        if (!this.expected(TokenType.L_CURLY))
            return null;

        Token opening = this.tokenStream.current();
        this.tokenStream.advance();

        List<Statement> statements = new ArrayList<>();
        while (this.tokenStream.matches(TokenType.R_CURLY)) {
            Statement statement = this.parseStatementForBody();
            if (statement == null)
                continue;

            statements.add(statement);
        }

        this.expected(TokenType.R_CURLY);
        Token closing = this.tokenStream.current();
        this.tokenStream.advance();

        return new BodyStatement(opening.getPosition(),closing.getPosition(), statements);
    }

    /**
     * Parse body statements for scoped areas like functions bodies.
     * contains:

     * x if, else if, else
     * - switch
     * - match
     * x loop
     * x for
     * x var
     * x while
     * x do-while
     * x unreachable

     * - return
     * - yield
     * - continue
     * - break

     * @return the statement parsed
     */
    public Statement parseStatementForBody() {
        if (isType(this.tokenStream.current())) {
            return this.parseVariableStatement();
        }
        if (this.tokenStream.matches(TokenType.IF)) {
            return this.parseIfStatement();
        }
        if (this.tokenStream.matches(TokenType.UNREACHABLE)) {
            return this.parseUnreachable();
        }
        if (this.tokenStream.matches(TokenType.WHILE)) {
            return this.parseWhileStatement();
        }
        if (this.tokenStream.matches(TokenType.DO)) {
            return this.parseDoWhileStatement();
        }
        if (this.tokenStream.matches(TokenType.LOOP)) {
            return this.parseLoopStatement();
        }
        if (this.tokenStream.matches(TokenType.FOR)) {
            return this.parseForStatement();
        }
        if (this.tokenStream.matches(TokenType.SWITCH)) {
            return this.parseSwitchStatement();
        }

        return null; // todo write this
    }

    private SwitchStatement parseSwitchStatement() throws ParseException {
        this.tokenStream.advance();

        Token start = this.tokenStream.current();

        if (!this.expected(TokenType.L_PAREN))
            return null;
        this.tokenStream.advance();

        Expression expression = parseExpression();

        if (!this.expected(TokenType.R_PAREN))
            return null;
        this.tokenStream.advance();

        if (!this.expected(TokenType.L_CURLY))
            return null;
        this.tokenStream.advance();
        List<SwitchStatement.CaseElement> caseElements = new ArrayList<>();

        while (this.tokenStream.matches(TokenType.R_CURLY)) {
            if (this.tokenStream.current().getType().equals(TokenType.DEFAULT) ||
                    this.tokenStream.current().getType().equals(TokenType.CASE)) {
                List<Expression> conditions = new ArrayList<>();
                boolean defaultState = false;

                if (this.tokenStream.matches(TokenType.CASE)) {
                    this.tokenStream.advance();

                    while (!this.tokenStream.current().getType().equals(TokenType.LAMBDA) &&
                            !this.tokenStream.current().getType().equals(TokenType.COLON)) {
                        conditions.add(parseExpression());

                        if (!this.tokenStream.current().getType().equals(TokenType.LAMBDA) &&
                                !this.tokenStream.current().getType().equals(TokenType.COLON)) {
                            if (!this.expected(TokenType.COMMA))
                                return null;
                            this.tokenStream.advance();
                        }
                    }
                }
                if (this.tokenStream.matches(TokenType.DEFAULT)) {
                    if (caseElements.stream().anyMatch(SwitchStatement.CaseElement::isDefaultState)) {
                        createSyntaxError("default statement already defined!");
                    }

                    this.tokenStream.advance();
                    defaultState = true;
                    // we don't have any conditions!
                }

                Statement body = null;
                if (this.tokenStream.current().getType().equals(TokenType.LAMBDA) ||
                        this.tokenStream.current().getType().equals(TokenType.COLON)) {
                    this.tokenStream.advance();

                    if (this.expected(TokenType.L_CURLY)) {
                        body = parseBodyStatement();
                    } else {
                        body = parseStatementForBody();
                    }
                }

                caseElements.add(new SwitchStatement.CaseElement(defaultState,
                        conditions.toArray(new Expression[0]), body));

                continue;
            }

            createSyntaxError(start, "expected 'case' or 'default' but got '%s'",
                    this.tokenStream.current().getType());
        }

        if (!this.expected(TokenType.R_CURLY))
            return null;
        this.tokenStream.advance();

        if (caseElements.isEmpty()) {
            createSyntaxError(start, "can't compile switch statement with no cases!");
            return null;
        }
        if (caseElements.size() == 1 && caseElements.stream().anyMatch(SwitchStatement.CaseElement::isDefaultState)) {
            createSyntaxError(start, "can't compile switch statement with only default case!");
            return null;
        }

        return new SwitchStatement(expression, caseElements.toArray(new SwitchStatement.CaseElement[0]));
    }

    public Statement parseForStatement() {
        this.tokenStream.advance();

        if (!this.expected(TokenType.L_PAREN))
            return null;
        this.tokenStream.advance();

        ForStatement.ForCondition forCondition = null;

        // for (name: type -> expr)
        if (this.tokenStream.matches(TokenType.LITERAL) &&
                this.tokenStream.peak(1).getType().equals(TokenType.COLON)) {

            String name = this.tokenStream.current().getValue();
            this.tokenStream.advance();

            if (!this.expected(TokenType.COLON))
                return null;
            this.tokenStream.advance();

            if (!this.isType(this.tokenStream.current())) {
                createSyntaxError("expected type but got '%s'", this.tokenStream.current());
                return null;
            }
            ParsedType type = this.parseType();

            if (!this.expected(TokenType.LAMBDA))
                return null;
            this.tokenStream.advance();

            Expression expression = this.parseExpression();

            if (!this.expected(TokenType.L_PAREN))
                return null;
            this.tokenStream.advance();

            forCondition = new ForStatement.IterateCondition(type, name, expression);
        } else { // for (var; expr; expr)
            Statement start = this.parseVariableStatement(Accessibility.PRIVATE);

            if (!this.expected(TokenType.SEMICOLON))
                return null;
            this.tokenStream.advance();

            Expression condition = this.parseExpression();

            if (!this.expected(TokenType.SEMICOLON))
                return null;
            this.tokenStream.advance();

            Expression appliedAction = this.parseExpression();

            forCondition = new ForStatement.NumberRangeCondition(start, condition, appliedAction);
        }

        BodyStatement bodyStatement = this.parseBodyStatement();

        return new ForStatement(forCondition, bodyStatement);
    }

    public Statement parseLoopStatement() {
        this.tokenStream.advance();

        BodyStatement bodyStatement = this.parseBodyStatement();

        return new LoopStatement(bodyStatement);
    }

    public Statement parseDoWhileStatement() {
        this.tokenStream.advance();

        BodyStatement bodyStatement = this.parseBodyStatement();

        if (!expected(TokenType.WHILE))
            return null;
        this.tokenStream.advance();

        if (!this.expected(TokenType.L_PAREN))
            return null;
        this.tokenStream.advance();

        Expression condition = this.parseExpression();

        if (!this.expected(TokenType.R_PAREN))
            return null;
        this.tokenStream.advance();

        return new DoWhileStatement(condition, bodyStatement);
    }

    public Statement parseWhileStatement() {
        this.tokenStream.advance();

        if (!this.expected(TokenType.L_PAREN))
            return null;
        this.tokenStream.advance();

        Expression condition = this.parseExpression();

        if (!this.expected(TokenType.R_PAREN))
            return null;
        this.tokenStream.advance();

        BodyStatement bodyStatement = this.parseBodyStatement();

        return new WhileStatement(condition, bodyStatement);
    }

    public Statement parseUnreachable() {
        this.tokenStream.advance();

        if (!this.expected(TokenType.SEMICOLON))
            return null;
        this.tokenStream.advance();

        return new UnreachableStatement();
    }

    public Statement parseIfStatement() {
        this.tokenStream.advance();

        if (!this.expected(TokenType.L_PAREN))
            return null;
        this.tokenStream.advance();

        Expression condition = this.parseExpression();

        if (!this.expected(TokenType.R_PAREN))
            return null;
        this.tokenStream.advance();

        BodyStatement bodyStatement = this.parseBodyStatement();

        if (this.tokenStream.matches(TokenType.ELSE)) {
            this.tokenStream.advance();

            Statement elseStatement = null;

            if (this.tokenStream.matches(TokenType.L_CURLY)) {
                elseStatement = this.parseBodyStatement();
            }
            if (this.tokenStream.matches(TokenType.IF)) {
                elseStatement = this.parseIfStatement(); // loop
            }

            return new IfStatement(condition, bodyStatement, elseStatement);
        }
        // no else statement :C
        return new IfStatement(condition, bodyStatement, null);
    }

    public Statement parseLinkingNotice() {
        if (!this.expected(TokenType.LITERAL))
            return null;

        StringBuilder path = new StringBuilder(this.tokenStream.current().getValue());
        this.tokenStream.advance();

        if (this.tokenStream.current().getType().equals(TokenType.DOT)) {
            this.tokenStream.advance();
            path.append("/");

            while (tokenStream.current().getType() != TokenType.SEMICOLON) {
                if (!this.expected(TokenType.LITERAL))
                    return null;

                path.append(this.tokenStream.current().getValue());
                this.tokenStream.advance();

                if (this.tokenStream.current().getType() == TokenType.SEMICOLON)
                    continue;

                if (!this.expected(TokenType.DOT))
                    return null;
                this.tokenStream.advance();

                path.append("/");
            }
        }

        if (!this.expected(TokenType.SEMICOLON))
            return null;
        this.tokenStream.advance();

        System.out.println(path);
        return new LinkedNoticeStatement(path.toString());
    }

    public Statement parseFunction(Accessibility... accessibility) {
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

    public boolean isType(Token token) {
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
