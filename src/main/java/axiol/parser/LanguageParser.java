package axiol.parser;

import axiol.Architecture;
import axiol.lexer.LanguageLexer;
import axiol.lexer.Token;
import axiol.lexer.TokenType;
import axiol.parser.expression.Operator;
import axiol.parser.scope.Namespace;
import axiol.parser.scope.ScopeStash;
import axiol.parser.statement.Accessibility;
import axiol.parser.statement.Parameter;
import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;
import axiol.parser.tree.RootNode;
import axiol.parser.tree.expressions.*;
import axiol.parser.tree.expressions.control.MatchExpression;
import axiol.parser.tree.expressions.extra.CastExpression;
import axiol.parser.tree.expressions.extra.ElementReferenceExpression;
import axiol.parser.tree.expressions.extra.StackAllocExpression;
import axiol.parser.tree.expressions.sub.BooleanExpression;
import axiol.parser.tree.expressions.sub.NumberExpression;
import axiol.parser.tree.expressions.sub.StringExpression;
import axiol.parser.tree.statements.BodyStatement;
import axiol.parser.tree.statements.EmptyStatement;
import axiol.parser.tree.statements.LinkedNoticeStatement;
import axiol.parser.tree.statements.VariableStatement;
import axiol.parser.tree.statements.control.*;
import axiol.parser.tree.statements.oop.*;
import axiol.parser.tree.statements.special.NativeStatement;
import axiol.parser.util.Parser;
import axiol.parser.util.SourceFile;
import axiol.parser.util.error.LanguageException;
import axiol.parser.util.error.Position;
import axiol.parser.util.error.TokenPosition;
import axiol.parser.util.reference.Reference;
import axiol.parser.util.reference.ReferenceType;
import axiol.parser.util.stream.TokenStream;
import axiol.types.*;
import axiol.types.custom.I128;
import axiol.types.custom.U128;

import java.io.File;
import java.util.*;

/**
 * The type Language parser.
 */
public class LanguageParser extends Parser {

    private static final EmptyStatement EMPTY_STATEMENT = new EmptyStatement();

    private final TokenType[] accessModifier = {
            TokenType.PUBLIC, TokenType.PRIVATE, TokenType.INLINE, TokenType.CONST,
            TokenType.EXTERN, TokenType.PROTECTED
    };
    
    private final TokenType[] valueContainingTypes = {
            // chars strings
            TokenType.STRING, TokenType.CHAR,
            // true false
            TokenType.BOOLEAN,
            // numbers
            TokenType.INT, TokenType.LONG, TokenType.DOUBLE,
            TokenType.FLOAT, TokenType.HEX_NUM,
            // big numbers
            TokenType.BIG_NUMBER, TokenType.BIG_HEX_NUM,
            // other
            TokenType.LITERAL
    };
    private final TokenType[] numberContainingTypes = {
            TokenType.INT, TokenType.LONG, TokenType.DOUBLE,
            TokenType.FLOAT, TokenType.HEX_NUM,

            TokenType.BIG_NUMBER, TokenType.BIG_HEX_NUM
    };

    private ScopeStash scopeStash;

    private TokenStream tokenStream;
    private String source;
    private String path;

    @Override
    public RootNode parseFile(File file) throws Throwable {
        StringBuilder builder = new StringBuilder();
        Scanner scanner = new Scanner(file);

        File folder = file.getParentFile();

        while (scanner.hasNextLine())
            builder.append(scanner.nextLine()).append("\n");

        scanner.close();
        return parseSource(folder, file.toPath().toString(), builder.toString());
    }

    @Override
    public RootNode parseSource(File folder, String path, String content) {
        SourceFile sourceFile = new SourceFile(folder, path, content);
        LanguageLexer lexer = new LanguageLexer();

        this.scopeStash = new ScopeStash();
        RootNode rootNode = new RootNode(sourceFile, scopeStash);

        List<Token> tokens = lexer.tokenizeString(content);

        this.tokenStream = new TokenStream(sourceFile, tokens);
        this.source = content;
        this.path = path;

        while (tokenStream.hasMoreTokens()) {
            Statement statement = this.parseStatement();

            if (statement != null)
                rootNode.getStatements().add(statement);
        }

        return rootNode;
    }

    /**
     * Parse body statements for global .
     * contains:
     * x functions
     * x structures
     * x class
     * x global var
     * x import
     * - attributes
     *
     * @return the statement parsed
     */
    public Statement parseStatement() {
        if ((isAccessModifier() && isType()) || isType()) {
            if (isAccessModifier()) {
                return this.parseVariableStatement(false, this.parseAccess());
            }
            return this.parseVariableStatement(false);
        }
        if ((isAccessModifier() && this.tokenStream.peak(1).getType().equals(TokenType.CLASS)) ||
                this.tokenStream.matches(TokenType.CLASS)) {
            if (isAccessModifier()) {
                return this.parseClassTypeStatement(this.parseAccess());
            }
            return this.parseClassTypeStatement();
        }
        if ((isAccessModifier() && this.tokenStream.peak(1).getType().equals(TokenType.NAMESPACE)) ||
                this.tokenStream.matches(TokenType.NAMESPACE)) {
            if (isAccessModifier()) {
                return this.parseNamespaceStatement(this.parseAccess());
            }
            return this.parseNamespaceStatement();
        }
        if ((isAccessModifier() && this.tokenStream.peak(1).getType().equals(TokenType.CONSTRUCT)) ||
                this.tokenStream.matches(TokenType.CONSTRUCT)) {
            if (isAccessModifier()) {
                return this.parseConstructStatement(this.parseAccess());
            }
            return this.parseConstructStatement();
        }
        if ((isAccessModifier() && this.tokenStream.peak(1).getType().equals(TokenType.STRUCTURE)) ||
                this.tokenStream.matches(TokenType.STRUCTURE)) {
            if (isAccessModifier()) {
                return this.parseStructStatement(this.parseAccess());
            }
            return this.parseStructStatement();
        }

        if ((isAccessModifier() && this.tokenStream.peak(1).getType().equals(TokenType.FUNCTION))
                || this.tokenStream.matches(TokenType.FUNCTION)) {
            if (isAccessModifier()) {
                return this.parseFunction(this.parseAccess());
            }
            return this.parseFunction();
        }
        if (this.tokenStream.matches(TokenType.LINKED)) {
            this.tokenStream.advance();

            return this.parseLinkingNotice();
        }

        Token unidentified = this.isAccessModifier() ? this.tokenStream.peak(1) : this.tokenStream.current();
        this.createSyntaxError(unidentified, "statement not suited for parsing with token '%s'", unidentified);
        return null;
    }

    public Namespace readNamespace() {
        List<String> namespaceParts = new ArrayList<>();
        while (this.tokenStream.peak(1).getType() == TokenType.FN_ACCESS) {
            this.expected(TokenType.LITERAL);
            namespaceParts.add(this.tokenStream.current().getValue());
            this.tokenStream.advance();
            this.tokenStream.advance();
        }

        Namespace namespace = scopeStash.resolveNamespace(namespaceParts);
        if (namespace == null) {
            namespace = scopeStash.importNamespace(namespaceParts);
        }

        return namespace;
    }

    public Statement parseConstructStatement(Accessibility... accessibility) {
        TokenPosition position = this.tokenStream.currentPosition();
        this.tokenStream.advance();

        List<Parameter> parameters = this.parseParameters(TokenType.L_PAREN, TokenType.R_PAREN);

        BodyStatement bodyStatement = this.parseBodyStatement();

        return new ConstructStatement(accessibility, parameters, bodyStatement, position);
    }

    private Statement parseClassTypeStatement(Accessibility... accessibility) {
        this.tokenStream.advance();

        this.expected(TokenType.LITERAL);
        String className = this.tokenStream.current().getValue();
        TokenPosition position = this.tokenStream.currentPosition();
        this.tokenStream.advance();

        String Class = null;
        if (this.tokenStream.matches(TokenType.PARENT)) {
            this.tokenStream.advance();

            this.expected(TokenType.LITERAL);
            Class = this.tokenStream.current().getValue();
            this.tokenStream.advance();
        }

        BodyStatement bodyStatement = this.parseClassBodyStatement();

        return new ClassTypeStatement(accessibility, className, Class, bodyStatement, null, position);
    }

    private Statement parseNamespaceStatement(Accessibility... accessibility) {
        this.tokenStream.advance();

        TokenPosition tokenPosition = this.tokenStream.currentPosition();

        int namespaceCount = 0;
        do {
            expected(TokenType.LITERAL);
            String namespaceName = this.tokenStream.current().getValue();
            this.tokenStream.advance();

            scopeStash.pushNamespace(namespaceName);
            namespaceCount++;

            if (this.tokenStream.current().getType() == TokenType.L_CURLY) {
                break;
            }

            expected(TokenType.FN_ACCESS);
            this.tokenStream.advance();
        } while (true);

        Reference namespace = scopeStash.getNamespaceReference();
        BodyStatement bodyStatement = this.parseClassBodyStatement();

        NamespaceStatement stat = new NamespaceStatement(tokenPosition, namespace, bodyStatement);

        for (int i = 0; i < namespaceCount; i++) {
            scopeStash.popNamespace();
        }

        return stat;
    }


    private Statement parseStructStatement(Accessibility... accessibility) {
        this.tokenStream.advance();

        if (!this.tokenStream.matches(TokenType.LITERAL)) {
            return null;
        }
        String structName = this.tokenStream.current().getValue();
        TokenPosition position = this.tokenStream.currentPosition();
        this.tokenStream.advance();

        List<Parameter> parameters = this.parseParameters(TokenType.L_CURLY, TokenType.R_CURLY);

        return new StructTypeStatement(parameters, structName, accessibility, null, position);
    }

    public BodyStatement parseClassBodyStatement() {
        if (!this.expected(TokenType.L_CURLY))
            return null;

        Token opening = this.tokenStream.current();
        this.tokenStream.advance();

        List<Statement> statements = new ArrayList<>();
        while (!this.tokenStream.matches(TokenType.R_CURLY)) {
            Statement statement = this.parseStatement();

            if (statement == null)
                continue;

            statements.add(statement);

        }

        this.expected(TokenType.R_CURLY);
        this.tokenStream.advance();

        return new BodyStatement(opening.getTokenPosition(), statements);
    }

    public BodyStatement parseBodyStatement() {
        if (!this.expected(TokenType.L_CURLY))
            return null;

        Token opening = this.tokenStream.current();
        this.tokenStream.advance();

        List<Statement> statements = new ArrayList<>();
        while (!this.tokenStream.matches(TokenType.R_CURLY)) {
            Statement statement = this.parseStatementForBody();

            if (statement == null)
                continue;

            statements.add(statement);
        }

        this.expected(TokenType.R_CURLY);
        this.tokenStream.advance();

        return new BodyStatement(opening.getTokenPosition(), statements);
    }

    /**
     * Parse body statements for d areas like functions bodies.
     * contains:

     * x if, else if, else
     * x switch
     * x loop
     * x for
     * x var
     * x while
     * x do-while

     * x unreachable
     * x return
     * x yield
     * x continue
     * x break

     * x asm
     * x inset
     * - stack-alloc
     * - malloc

     * x constructor (class only)

     * @return the statement parsed
     */
    public Statement parseStatementForBody() {
        if (isType()) {
            return this.parseVariableStatement(true);
        }
        if (isUDTDefinition()) {
            return this.parseUDTDeclare();
        }
        if (this.tokenStream.matches(TokenType.IF)) {
            return this.parseIfStatement();
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

        // ir or asm modifying statements
        if (this.tokenStream.matches(TokenType.NATIVE)) {
            return this.parseNativeStatement();
        }

        // one line statements
        if (this.tokenStream.matches(TokenType.UNREACHABLE)) {
            return this.parseUnreachable();
        }
        if (this.tokenStream.matches(TokenType.RETURN)) {
            TokenPosition position = this.tokenStream.currentPosition();
            this.tokenStream.advance();

            Expression value = this.parseExpression(Type.NONE);

            expectLineEnd();
            return new ReturnStatement(value, position);
        }
        if (this.tokenStream.matches(TokenType.YIELD)) {
            TokenPosition position = this.tokenStream.currentPosition();
            this.tokenStream.advance();

            Expression value = this.parseExpression(Type.NONE);

            expectLineEnd();
            return new YieldStatement(value, position);
        }
        if (this.tokenStream.matches(TokenType.CONTINUE)) {
            TokenPosition position = this.tokenStream.currentPosition();
            this.tokenStream.advance();

            expectLineEnd();
            return new ContinueStatement(position);
        }
        if (this.tokenStream.matches(TokenType.BREAK)) {
            TokenPosition position = this.tokenStream.currentPosition();
            this.tokenStream.advance();

            expectLineEnd();
            return new BreakStatement(position);
        }
        Expression expression = this.parseExpression(Type.NONE);
        if (expression != null) {
            if (this.tokenStream.matches(TokenType.SEMICOLON))
                this.tokenStream.advance();

            return expression;
        }

        createSyntaxError("no matching statement found for '%s'", this.tokenStream.current().getType());
        return null;
    }

    private NativeStatement parseNativeStatement() {
        this.tokenStream.advance();
        if (!this.expected(TokenType.L_SQUARE)) {
            return null;
        }
        this.tokenStream.advance();

        TokenPosition position = this.tokenStream.currentPosition();
        NativeStatement.Type type = this.tokenStream.current().getType() == TokenType.ASM ? NativeStatement.Type.ASM :
                this.tokenStream.current().getType() == TokenType.ISA ? NativeStatement.Type.IR : null;

        if (type == null) {
            this.createSyntaxError(this.tokenStream.current(),
                    "expected 'asm', 'inse' but got '%s'", this.tokenStream.current());
        }
        this.tokenStream.advance();

        Architecture architecture = Architecture.IR;
        if (type == NativeStatement.Type.ASM) {
            if (this.expected(TokenType.COMMA)) {
                this.tokenStream.advance();
            }
            String arch = this.tokenStream.current().getValue();
            architecture = Architecture.valueOf(arch.toUpperCase(Locale.ROOT));
            this.tokenStream.advance();
        }

        if (!this.expected(TokenType.R_SQUARE)) {
            return null;
        }
        this.tokenStream.advance();

        if (!this.expected(TokenType.L_CURLY)) {
            return null;
        }
        this.tokenStream.advance();

        List<NativeStatement.NativeInstruction> instructions = new ArrayList<>();

        while (!this.tokenStream.matches(TokenType.R_CURLY)) {
            this.expected(TokenType.STRING);
            String line = this.tokenStream.current().getValue();
            this.tokenStream.advance();
            List<Expression> params = new ArrayList<>();

            if (this.tokenStream.matches(TokenType.REV_LAMBDA)) {
                this.tokenStream.advance();

                while (!this.tokenStream.matches(TokenType.STRING) &&
                        !this.tokenStream.matches(TokenType.R_CURLY)) {
                    Expression expression = this.parseExpression(Type.NONE);

                    params.add(expression);

                    if (this.tokenStream.matches(TokenType.COMMA)) {
                        this.tokenStream.advance();
                    }
                }
            }
            instructions.add(new NativeStatement.NativeInstruction(line, params));

        }
        this.tokenStream.matches(TokenType.R_CURLY);
        this.tokenStream.advance();

        return new NativeStatement(position, type, architecture, instructions);
    }

    private SwitchStatement parseSwitchStatement() {
        TokenPosition position = this.tokenStream.currentPosition();
        this.tokenStream.advance();

        Token start = this.tokenStream.current();

        if (!this.expected(TokenType.L_PAREN))
            return null;
        this.tokenStream.advance();

        Expression expression = parseExpression(Type.NONE);

        if (!this.expected(TokenType.R_PAREN))
            return null;
        this.tokenStream.advance();

        if (!this.expected(TokenType.L_CURLY))
            return null;
        this.tokenStream.advance();
        List<SwitchStatement.CaseElement> caseElements = new ArrayList<>();

        while (!this.tokenStream.matches(TokenType.R_CURLY)) {

            if (this.tokenStream.matches(TokenType.DEFAULT) ||
                    this.tokenStream.matches(TokenType.CASE)) {
                List<Expression> conditions = new ArrayList<>();
                boolean defaultState = false;

                if (this.tokenStream.matches(TokenType.CASE)) {
                    this.tokenStream.advance();

                    while (!this.tokenStream.matches(TokenType.LAMBDA) &&
                            !this.tokenStream.matches(TokenType.COLON)) {
                        conditions.add(parseExpression(Type.NONE, 0));

                        if (!this.tokenStream.matches(TokenType.LAMBDA) &&
                                !this.tokenStream.matches(TokenType.COLON)) {
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
                if (this.tokenStream.matches(TokenType.LAMBDA) ||
                        this.tokenStream.matches(TokenType.COLON)) {
                    this.tokenStream.advance();

                    if (this.tokenStream.matches(TokenType.L_CURLY)) {
                        body = parseBodyStatement();
                    } else {
                        body = parseStatementForBody();
                    }
                } else {
                    expected(TokenType.LAMBDA);
                }

                caseElements.add(new SwitchStatement.CaseElement(defaultState,
                        conditions.toArray(new Expression[0]), body == null ? EMPTY_STATEMENT : body));

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

        return new SwitchStatement(expression, caseElements.toArray(new SwitchStatement.CaseElement[0]), position);
    }

    public Statement parseForStatement() {
        TokenPosition position = this.tokenStream.currentPosition();
        this.tokenStream.advance();

        if (!this.expected(TokenType.L_PAREN))
            return null;
        this.tokenStream.advance();

        ForStatement.ForCondition forCondition;

        // for (name: type -> expr)
        if (this.tokenStream.matches(TokenType.LITERAL) &&
                this.tokenStream.peak(1).getType().equals(TokenType.COLON)) {

            String name = this.tokenStream.current().getValue();
            this.tokenStream.advance();

            if (!this.expected(TokenType.COLON))
                return null;
            this.tokenStream.advance();

            if (!this.isType()) {
                createSyntaxError("expected type but got '%s'", this.tokenStream.current());
                return null;
            }
            Type type = this.parseType();

            if (!this.expected(TokenType.LAMBDA))
                return null;
            this.tokenStream.advance();

            Expression expression = this.parseExpression(type);

            if (!this.expected(TokenType.R_PAREN))
                return null;
            this.tokenStream.advance();

            forCondition = new ForStatement.IterateCondition(null, expression);
        } else { // for (var; expr; expr)
            Statement start = this.parseVariableStatement(true, Accessibility.PRIVATE);

            Expression condition = this.parseExpression(Type.NONE);

            if (!this.expected(TokenType.SEMICOLON))
                return null;
            this.tokenStream.advance();

            Expression appliedAction = this.parseExpression(Type.NONE);

            forCondition = new ForStatement.NumberRangeCondition(start, condition, appliedAction);

            if (!this.expected(TokenType.R_PAREN)){
                return null;
            }
            this.tokenStream.advance();
        }

        BodyStatement bodyStatement = this.parseBodyStatement();

        return new ForStatement(forCondition, bodyStatement, position);
    }

    public Statement parseLoopStatement() {
        TokenPosition position = this.tokenStream.currentPosition();
        this.tokenStream.advance();

        BodyStatement bodyStatement = this.parseBodyStatement();

        return new LoopStatement(bodyStatement, position);
    }

    public Statement parseDoWhileStatement() {
        TokenPosition position = this.tokenStream.currentPosition();
        this.tokenStream.advance();

        BodyStatement bodyStatement = this.parseBodyStatement();

        if (!expected(TokenType.WHILE))
            return null;
        this.tokenStream.advance();

        if (!this.expected(TokenType.L_PAREN))
            return null;
        this.tokenStream.advance();

        Expression condition = this.parseExpression(Type.NONE);

        if (!this.expected(TokenType.R_PAREN))
            return null;
        this.tokenStream.advance();

        if (!this.expected(TokenType.SEMICOLON))
            return null;
        this.tokenStream.advance();

        return new DoWhileStatement(condition, bodyStatement, position);
    }

    public Statement parseWhileStatement() {
        TokenPosition position = this.tokenStream.currentPosition();
        this.tokenStream.advance();

        if (!this.expected(TokenType.L_PAREN))
            return null;
        this.tokenStream.advance();

        Expression condition = this.parseExpression(Type.NONE);

        if (!this.expected(TokenType.R_PAREN))
            return null;
        this.tokenStream.advance();

        BodyStatement bodyStatement = this.parseBodyStatement();

        return new WhileStatement(condition, bodyStatement, position);
    }

    public Statement parseUnreachable() {
        TokenPosition position = this.tokenStream.currentPosition();
        this.tokenStream.advance();

        if (!this.expected(TokenType.SEMICOLON))
            return null;
        this.tokenStream.advance();

        return new UnreachableStatement(position);
    }

    public Statement parseIfStatement() {
        this.tokenStream.advance();

        if (!this.expected(TokenType.L_PAREN))
            return null;
        this.tokenStream.advance();

        TokenPosition position = this.tokenStream.currentPosition();
        Expression condition = this.parseExpression(Type.NONE);

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

            return new IfStatement(condition, bodyStatement, elseStatement == null ? EMPTY_STATEMENT : elseStatement, position);
        }
        // no else statement :C
        return new IfStatement(condition, bodyStatement, EMPTY_STATEMENT, position);
    }

    public Statement parseLinkingNotice() {
        if (!this.expected(TokenType.LITERAL))
            return null;

        StringBuilder path = new StringBuilder(this.tokenStream.current().getValue());
        TokenPosition position = this.tokenStream.currentPosition();
        this.tokenStream.advance();

        if (this.tokenStream.matches(TokenType.DOT)) {
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

        return new LinkedNoticeStatement(path.toString(), position);
    }

    public Statement parseFunction(Accessibility... accessibility) {
        this.tokenStream.advance();

        if (!this.tokenStream.matches(TokenType.LITERAL)) {
            return null;
        }
        String functionName = this.tokenStream.current().getValue();
        TokenPosition position = this.tokenStream.currentPosition();

        this.tokenStream.advance();

        List<Parameter> parameters = this.parseParameters(TokenType.L_PAREN, TokenType.R_PAREN);

        Type returnType = Type.VOID;
        if (this.tokenStream.matches(TokenType.LAMBDA)) {
            this.tokenStream.advance();

            returnType = this.parseType();
        }

        BodyStatement bodyStatement = this.parseBodyStatement();

        Reference reference = scopeStash.getFunctionScope().addFunction(returnType,
                scopeStash.getNamespace(),
                functionName, parameters);

        if (reference == null) {
            Reference blocker = scopeStash.getFunctionScope().getFunctionBlocking(scopeStash.getNamespace(),
                    functionName, returnType, parameters);

            TokenPosition syntaxPosition = scopeStash.getFirstReferencePosition(blocker);
            Position startPos = syntaxPosition == null ? null : syntaxPosition.getStart();

            createSyntaxError(
                    position,
                    "A function with the name '%s' already exists (line: %s, column: %s)",
                    functionName,
                    startPos == null ? "?" : (startPos.line() + 1),
                    startPos == null ? "?" : (startPos.column() + 1)
            );
        }

        // Always set reference position
        scopeStash.setReferencePosition(reference, position);

        FunctionStatement functionNameSyntax = new FunctionStatement(functionName, accessibility,
                        parameters, bodyStatement, returnType, null, position);

        scopeStash.getLocalScope().popLocals();

        return functionNameSyntax;
    }

    public List<Parameter> parseParameters(TokenType open, TokenType close) {
        List<Parameter> parameters = new ArrayList<>();

        if (!this.tokenStream.matches(open)) {
            return null;
        }
        this.tokenStream.advance();

        scopeStash.getLocalScope().pushBlock();
        scopeStash.getLocalScope().pushLocals();

        while (!this.tokenStream.matches(close)) {
            boolean pointer = false, referenced = false;
            if (this.tokenStream.matches(TokenType.MULTIPLY)) {
                this.tokenStream.advance();
                pointer = true;
            }
            if (this.tokenStream.matches(TokenType.AND)) {
                this.tokenStream.advance();
                referenced = true;
            }
            this.expected(TokenType.LITERAL);
            String parameterName = this.tokenStream.current().getValue();
            this.tokenStream.advance();

            this.expected(TokenType.COLON);
            this.tokenStream.advance();

            Type type = this.parseType();

            Reference reference = scopeStash.getLocalScope()
                    .addLocalVariable(scopeStash.getNamespaceRoot(), type, false, parameterName);

            if (this.tokenStream.matches(TokenType.COMMA)) {
                this.tokenStream.advance();

                parameters.add(new Parameter(parameterName, type, null, pointer, referenced, reference));
                continue;
            }
            if (this.tokenStream.matches(TokenType.EQUAL)) {
                this.tokenStream.advance();

                Expression defaultValue = parseExpression(type, 0);
                parameters.add(new Parameter(parameterName, type, defaultValue, pointer, referenced, reference));
                continue;
            }

            parameters.add(new Parameter(parameterName, type, null, pointer, referenced, reference));
        }
        if (!this.tokenStream.matches(close)) {
            return null;
        }
        this.tokenStream.advance();

        return parameters;
    }

    public Statement parseUDTDeclare() {
        this.expected(TokenType.LITERAL);
        String udtType = this.tokenStream.current().getValue();
        this.tokenStream.advance();

        this.expected(TokenType.COLON);
        this.tokenStream.advance();

        this.expected(TokenType.LITERAL);
        String udtName = this.tokenStream.current().getValue();
        TokenPosition position = this.tokenStream.currentPosition();
        this.tokenStream.advance();

        this.expected(TokenType.EQUAL);
        this.tokenStream.advance();

        this.expected(TokenType.L_PAREN);
        this.tokenStream.advance();

        List<Expression> parameters = new ArrayList<>();
        while (!this.tokenStream.matches(TokenType.R_PAREN)) {
            Expression expression = this.parseExpression(Type.NONE);

            if (expression != null)
                parameters.add(expression);

            if (this.tokenStream.matches(TokenType.COMMA))
                this.tokenStream.advance();
        }
        this.expected(TokenType.R_PAREN);
        this.tokenStream.advance();

        if (this.tokenStream.matches(TokenType.SEMICOLON))
            this.tokenStream.advance();

        return new UDTDeclareStatement(udtType, udtName, parameters, null, position);
    }

    public Statement parseVariableStatement(boolean local, Accessibility... accessibility) {
        Type type = this.parseType();

        if (!expected(TokenType.LITERAL))
            return null;

        String name = this.tokenStream.current().getValue();

        TokenPosition position = this.tokenStream.currentPosition();
        this.tokenStream.advance();

        Expression initExpression = null;
        if (this.tokenStream.matches(TokenType.EQUAL)) {
            this.tokenStream.advance();

            initExpression = this.parseExpression(type);
        }

        if (this.tokenStream.matches(TokenType.SEMICOLON))
            this.tokenStream.advance();

        Namespace namespace = local ? scopeStash.getNamespace()
                : scopeStash.getNamespaceRoot();



        if (scopeStash.getLocalScope().getVariable(namespace, name) != null) {

            createSyntaxError(position, "A %s variable '%s' has already been defined", "local", name);
        }

        Reference reference = scopeStash.getLocalScope().addLocalVariable(namespace, type, local, name);
        return new VariableStatement(name, type, initExpression, reference, position, accessibility);
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

    public boolean isUDTDefinition() {
        return this.tokenStream.current().getType().equals(TokenType.LITERAL) && this.tokenStream.peak(1).getType() == TokenType.COLON;
    }

    public boolean isType() {
        int peak = 0;
        if (isAccessModifier()) {
            peak = 1;
        }
        if (this.tokenStream.peak(peak).getType() == TokenType.MULTIPLY) {
            while (this.tokenStream.peak(peak).getType() == TokenType.MULTIPLY)
                peak++;
        }

        return !Type.typeByToken(this.tokenStream.peak(peak)).equals(Type.NONE);
    }

    public boolean isAccessModifier() {
        return Arrays.stream(this.accessModifier).anyMatch(type -> type == this.tokenStream.current().getType());
    }

    public Type parseType() {
        int pointerDepth = 0;
        if (this.tokenStream.matches(TokenType.MULTIPLY)) {
            while (this.tokenStream.matches(TokenType.MULTIPLY)) {
                this.tokenStream.advance();
                pointerDepth++;
            }
        }

        Token typeToken = this.tokenStream.current();
        Type type = Type.typeByToken(typeToken);
        this.tokenStream.advance();

        int arrayDepth = 0;
        while (this.tokenStream.matches(TokenType.L_SQUARE)) {
            this.tokenStream.advance();

            this.expected(TokenType.R_SQUARE);
            this.tokenStream.advance();
            arrayDepth++;
        }

        return new Type(type, arrayDepth, pointerDepth);
    }

    public void expectLineEnd() {
        if (this.expected(TokenType.SEMICOLON))
            this.tokenStream.advance();
    }

    @Override
    public Expression parseExpression(Type type) {
        return parseExpression(type, Operator.MAX_PRIORITY);
    }

    /*
     * x array = {expr, expr}
     * x array = [10];
     * x array = [_];
     *
     * x var = expr ? expr : expr;
     *
     * x ref = &expression;
     * x ref = cast[type] expression;
     * x ref = stackAlloc[type];
     *
     * x var = test.t;
     * - var = test.*t; TODO
     * - var = test::test::fn();
     *
     * - var = (test) -> null;
     * - var = (test) -> {};
     * - var = () -> null;
     * - var = () -> {};
     **/
    public Expression parseExpression(Type simpleType, int priority) {
        if (priority < 0)
            priority = 0;

        Operator[] operators = Operator.getOperatorsByPriority(priority).toArray(new Operator[0]);

        if (priority == 0) {

            // &expr
            if (tokenStream.matches(TokenType.AND)) {
                this.tokenStream.advance();

                return new ElementReferenceExpression(this.parseExpression(simpleType, Operator.MAX_PRIORITY), this.tokenStream.currentPosition());
            }
            // [_] empty array 0 elements
            // [expression] sized empty array
            if (tokenStream.matches(TokenType.L_SQUARE)) {
                this.tokenStream.advance();

                if (this.tokenStream.matches(TokenType.UNDERSCORE)) {
                    this.tokenStream.advance();

                    expected(TokenType.R_SQUARE);
                    Token current = this.tokenStream.current();
                    this.tokenStream.advance();

                    return new ArrayInitExpression(new ArrayList<>(), simpleType, new NumberExpression(
                            current.getTokenPosition(), 0, Type.I32, true), this.tokenStream.currentPosition());
                }
                Expression expression = this.parseExpression(simpleType, 0);

                expected(TokenType.R_SQUARE);
                this.tokenStream.advance();

                return new ArrayInitExpression(new ArrayList<>(), simpleType, expression, this.tokenStream.currentPosition());
            }
            if (tokenStream.matches(TokenType.CAST)) {
                TokenPosition tokenPosition = tokenStream.currentPosition();
                this.tokenStream.advance();

                expected(TokenType.L_SQUARE);
                this.tokenStream.advance();

                Type type = parseType();

                expected(TokenType.R_SQUARE);
                this.tokenStream.advance();

                Expression expression = parseExpression(type);
                return new CastExpression(tokenPosition, type, expression);
            }
            if (tokenStream.matches(TokenType.STACK_ALLOC)) {
                TokenPosition tokenPosition = tokenStream.currentPosition();
                this.tokenStream.advance();

                expected(TokenType.L_SQUARE);
                this.tokenStream.advance();

                Type type = parseType();

                Expression depth = null;
                if (this.tokenStream.matches(TokenType.COMMA)) {
                    this.tokenStream.advance();

                    depth = parseExpression(type, 0);

                    if (depth instanceof NumberExpression expression) {
                        expected(TokenType.R_SQUARE);
                        this.tokenStream.advance();

                        return new StackAllocExpression(tokenPosition, type, expression);
                    }
                    createSyntaxError(depth.position(), "expected number but got %s", depth.type().name());
                }
                expected(TokenType.R_SQUARE);
                this.tokenStream.advance();
                return new StackAllocExpression(tokenPosition, type,
                        new NumberExpression(tokenPosition, 1, Type.I32, true));
            }
            // {expr, expr, expr, ...}
            if (tokenStream.matches(TokenType.L_CURLY)) {
                this.tokenStream.advance();

                List<Expression> expressions = new ArrayList<>();

                while (!tokenStream.matches(TokenType.R_CURLY)) {
                    Expression element = this.parseExpression(simpleType, 0);
                    expressions.add(element);

                    if (this.tokenStream.matches(TokenType.R_CURLY))
                        continue;

                    if (!expected(TokenType.COMMA)) {
                        return null;
                    }
                    tokenStream.advance();
                }
                TokenPosition position = this.tokenStream.currentPosition();
                expected(TokenType.R_CURLY);
                this.tokenStream.advance();

                return new ArrayInitExpression(expressions, simpleType, new NumberExpression(
                        position, expressions.size(), Type.I32, true), this.tokenStream.currentPosition());
            }

            if (Arrays.stream(valueContainingTypes)
                    .anyMatch(type -> type.equals(this.tokenStream.current().getType()))) {
                return parseTypeExpression(simpleType);
            }
            if (tokenStream.matches(TokenType.MATCH)) {
                return this.parseMatchExpression(simpleType);
            }
            if (tokenStream.matches(TokenType.L_PAREN)) {
                this.tokenStream.advance();
                Expression expression = parseExpression(simpleType, Operator.MAX_PRIORITY);
                if (tokenStream.matches(TokenType.R_PAREN)) {
                    this.tokenStream.advance();
                } else {
                    createSyntaxError(
                            "expected closing parenthesis but got '%s'",
                            tokenStream.current().getValue());
                }
                return expression;
            }
            // atomics
        }

        int operatorCycles = 0;
        Expression leftAssociated = null;

        // unary
        for (Operator operator : operators) {
            if (!operator.isUnary() || operator.isLeftAssociated() ||
                    !this.tokenStream.matches(operator.getType()))
                continue;

            this.tokenStream.advance();

            leftAssociated = new UnaryExpression(operator, this.parseExpression(simpleType, priority),
                    this.tokenStream.currentPosition());
        }
        if (leftAssociated == null) {
            leftAssociated = this.parseExpression(simpleType, priority - 1);
        }

        // append last lef-associated Expression
        while (operatorCycles == 0) {
            operatorCycles = 1;

            for (Operator operator : operators) {
                if (!operator.isUnary() || !operator.isLeftAssociated() ||
                        !this.tokenStream.matches(operator.getType()))
                    continue;

                tokenStream.advance();
                operatorCycles = 0;

                leftAssociated = new UnaryExpression(operator, leftAssociated,
                        this.tokenStream.currentPosition());
            }
        }
        // reset for next loop
        operatorCycles = 0;

        // binary expression from past unary left
        while (operatorCycles == 0) {
            operatorCycles = 1;

            for (Operator operator : operators) {
                if (operator.isUnary() || !tokenStream.matches(operator.getType())) {
                    continue;
                }

                tokenStream.advance();
                operatorCycles = 0;

                Expression right = operator.isLeftAssociated() ?
                        parseExpression(simpleType, priority - 1) : parseExpression(simpleType, priority);

                leftAssociated = new BinaryExpression(operator, leftAssociated, right,
                        this.tokenStream.currentPosition());
            }
        }


        return leftAssociated;
    }

    private Expression parseTypeExpression(Type simpleType) {
        if (Arrays.stream(numberContainingTypes)
                .anyMatch(type -> type.equals(tokenStream.current().getType()))) {

            String tokenValue = this.tokenStream.current().getValue();
            Number value = 0; // default init 0
            boolean signed = true;

            if (this.tokenStream.matches(TokenType.HEX_NUM)) {
                value = Long.parseUnsignedLong(tokenValue.substring(2), 16);
            } else if (this.tokenStream.matches(TokenType.BIG_HEX_NUM)) {
                // todo
            } else {
                if (tokenValue.charAt(tokenValue.length() - 1) == 'u' ||
                        tokenValue.charAt(tokenValue.length() - 1) == 'U') {
                    signed = false;

                    tokenValue = tokenValue.substring(0, tokenValue.length() - 1);
                }

                if (this.tokenStream.matches(TokenType.BIG_NUMBER)) {
                    value = signed ? new I128(tokenValue) : new U128(tokenValue);
                } else {
                    value = Double.parseDouble(tokenValue);
                }

            }

            Type type = switch (this.tokenStream.current().getType()) {
                case INT, HEX_NUM -> signed ? Type.I32 : Type.U32;
                case DOUBLE, LONG -> signed ? Type.I64 : Type.U64;
                case FLOAT -> Type.F32;
                case SHORT -> signed ? Type.I16 : Type.U16;
                case BYTE -> signed ? Type.I8 : Type.U8;
                case BIG_NUMBER, BIG_HEX_NUM -> signed ? Type.I128 : Type.U128;
                default -> throw new IllegalArgumentException("Expected Number-Type but got '%s'"
                        .formatted(this.tokenStream.current().getType()));
            };

            NumberExpression numberExpression = new NumberExpression(
                    this.tokenStream.currentPosition(), value, type, signed);

            this.tokenStream.advance();
            return numberExpression;
        }
        if (tokenStream.matches(TokenType.CHAR)) {
            String singletonChar = this.tokenStream.current().getValue()
                    .substring(1, tokenStream.current().getValue().length() - 1);

            int value = singletonChar.charAt(0);

            NumberExpression numberExpression = new NumberExpression(
                    this.tokenStream.currentPosition(), value, Type.U8, false);

            this.tokenStream.advance();
            return numberExpression;
        }
        if (tokenStream.matches(TokenType.STRING)) {
            String singletonString = this.tokenStream.current().getValue()
                    .substring(1, tokenStream.current().getValue().length() - 1);

            StringExpression stringExpression = new StringExpression(
                    this.tokenStream.currentPosition(), singletonString);

            this.tokenStream.advance();
            return stringExpression;
        }
        if (tokenStream.matches(TokenType.BOOLEAN)) {
            BooleanExpression expression = new BooleanExpression(this.tokenStream.currentPosition(),
                    tokenStream.current().getValue().equals("true"));

            this.tokenStream.advance();
            return expression;
        }
        if (tokenStream.matches(TokenType.LITERAL)) {
            Namespace namespace = this.readNamespace();

            // function call!
            if (this.tokenStream.peak(1).getType() == TokenType.L_PAREN) {
                TokenPosition nameSyntaxPosition = this.tokenStream.currentPosition();

                String name = this.tokenStream.current().getValue();
                this.tokenStream.advance();

                expected(TokenType.L_PAREN);
                this.tokenStream.advance();

                List<Expression> parameters = new ArrayList<>();
                while (this.tokenStream.current().getType() != TokenType.R_PAREN) {
                    parameters.add(parseExpression(Type.NONE));

                    if (this.tokenStream.current().getType() == TokenType.COMMA) {
                        this.tokenStream.advance();
                        if (this.tokenStream.current().getType() == TokenType.R_PAREN) {
                            createSyntaxError("Invalid comma before ')'");
                        }
                    } else {
                        break;
                    }
                }

                List<Reference> simpleParameters = parameters.stream().map(i ->
                        new Reference(ReferenceType.PARAMETER, "", scopeStash.getNamespaceRoot(), i.valuedType())).toList();

                expected(TokenType.R_PAREN);

                Reference reference = scopeStash.getFunctionScope().getFunction(namespace, name, simpleParameters);
                if (reference == null) {
                    reference = scopeStash.getFunctionScope().importFunction(namespace, name, simpleParameters);
                    scopeStash.setReferencePosition(reference, nameSyntaxPosition);
                }
                this.tokenStream.advance();

                return new CallExpression(reference, parameters, nameSyntaxPosition);
            }

            String name = this.tokenStream.current().getValue();
            TokenPosition namePosition = this.tokenStream.currentPosition();
            String referenceName = name;

            if (this.tokenStream.peak(1).getType() == TokenType.DOT) {
                this.tokenStream.advance();

                expected(TokenType.DOT);
                this.tokenStream.advance();

                expected(TokenType.LITERAL);
                String innerName = this.tokenStream.current().getValue();
                referenceName = "%s.%s".formatted(name, innerName);
            }

            Reference reference = scopeStash.getLocalScope().getVariable(namespace, referenceName);
            if (reference == null) {
                reference = scopeStash.getLocalScope().importVariable(namespace, referenceName);
                scopeStash.setReferencePosition(reference, this.tokenStream.currentPosition());

                createSyntaxError("Could not find the variable '%s'", referenceName);
            }

            this.tokenStream.advance();

            return new LiteralExpression(reference, path.toString(), namePosition);
        }

        createSyntaxError(
                "invalid token for expression parsing: '%s'",
                this.tokenStream.current().getType());
        return null;
    }

    private MatchExpression parseMatchExpression(Type type) {
        this.tokenStream.advance();

        Token start = this.tokenStream.current();

        if (!expected(TokenType.L_PAREN))
            return null;
        this.tokenStream.advance();

        Expression expression = parseExpression(type, Operator.MAX_PRIORITY);

        if (!expected(TokenType.R_PAREN))
            return null;
        this.tokenStream.advance();

        if (!expected(TokenType.L_CURLY))
            return null;
        this.tokenStream.advance();

        List<MatchExpression.CaseElement> caseElements = new ArrayList<>();

        while (!this.tokenStream.matches(TokenType.R_CURLY)) {
            if (this.tokenStream.matches(TokenType.DEFAULT) ||
                    this.tokenStream.matches(TokenType.CASE)) {
                List<Expression> conditions = new ArrayList<>();
                boolean defaultState = false;

                if (this.tokenStream.matches(TokenType.CASE)) {
                    this.tokenStream.advance();

                    while (!this.tokenStream.matches(TokenType.LAMBDA) &&
                            !this.tokenStream.matches(TokenType.COLON)) {
                        conditions.add(parseExpression(type, Operator.MAX_PRIORITY));

                        if (!this.tokenStream.matches(TokenType.LAMBDA) &&
                                !this.tokenStream.matches(TokenType.COLON)) {
                            if (!expected(TokenType.COMMA))
                                return null;
                            this.tokenStream.advance();
                        }
                    }
                }
                if (this.tokenStream.matches(TokenType.DEFAULT)) {
                    if (caseElements.stream().anyMatch(MatchExpression.CaseElement::isDefaultState)) {
                        createSyntaxError("default statement already defined!");
                    }

                    this.tokenStream.advance();
                    defaultState = true;
                    // we don't have any conditions!
                }

                Expression body = null;
                if (this.tokenStream.matches(TokenType.LAMBDA)) {
                    this.tokenStream.advance();

                    body = parseExpression(type, Operator.MAX_PRIORITY);
                }

                if (this.tokenStream.matches(TokenType.SEMICOLON)) {
                    this.tokenStream.advance();
                }

                caseElements.add(new MatchExpression.CaseElement(defaultState, conditions.toArray(new Expression[0]), body));

                continue;
            }

            createSyntaxError(start, "expected 'case' or 'default' but got '%s'",
                    this.tokenStream.current().getType());
        }

        if (!expected(TokenType.R_CURLY))
            return null;
        this.tokenStream.advance();

        if (caseElements.isEmpty()) {
            createSyntaxError(start, "can't compile match expression with no cases!");
            return null;
        }
        if (caseElements.size() == 1 && caseElements.stream().anyMatch(MatchExpression.CaseElement::isDefaultState)) {
            createSyntaxError(start, "can't compile match expression with only default case!");
            return null;
        }


        return new MatchExpression(expression, caseElements.toArray(new MatchExpression.CaseElement[0]),
                this.tokenStream.currentPosition());
    }

    public boolean expected(TokenType type) {
        if (this.tokenStream.matches(type)) {
            return true;
        }
        createSyntaxError("unexpected token expected '%s' but got '%s'", type, this.tokenStream.current().getType());
        return false;
    }

    public void createSyntaxError(String message, Object... args) {
        LanguageException languageException = new LanguageException(source, this.tokenStream.current(), path, message, args);
        languageException.throwError();
    }

    public void createSyntaxError(Token position, String message, Object... args) {
        LanguageException languageException = new LanguageException(source, position, path, message, args);
        languageException.throwError();
    }

    public void createSyntaxError(TokenPosition position, String message, Object... args) {
        LanguageException languageException = new LanguageException(source, position, path, message, args);
        languageException.throwError();
    }

    public TokenStream getTokenStream() {
        return tokenStream;
    }

    public String getPath() {
        return path;
    }

    public String getSource() {
        return source;
    }

    public TokenType[] getAccessModifier() {
        return accessModifier;
    }
}
