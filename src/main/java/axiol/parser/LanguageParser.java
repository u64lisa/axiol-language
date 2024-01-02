package axiol.parser;

import axiol.Architecture;
import axiol.lexer.LanguageLexer;
import axiol.lexer.Token;
import axiol.lexer.TokenType;
import axiol.parser.expression.Operator;
import axiol.parser.statement.Accessibility;
import axiol.parser.statement.Parameter;
import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;
import axiol.parser.tree.RootNode;
import axiol.parser.tree.statements.BodyStatement;
import axiol.parser.tree.statements.EmptyStatement;
import axiol.parser.tree.statements.LinkedNoticeStatement;
import axiol.parser.tree.statements.VariableStatement;
import axiol.parser.tree.statements.control.*;
import axiol.parser.tree.statements.oop.*;
import axiol.parser.tree.statements.special.NativeStatement;
import axiol.parser.util.Parser;
import axiol.parser.util.scope.Scope;
import axiol.parser.util.SourceFile;
import axiol.parser.util.error.LanguageException;
import axiol.parser.util.error.TokenPosition;
import axiol.parser.util.scope.ScopeElement;
import axiol.parser.util.scope.ScopeElementType;
import axiol.parser.util.stream.TokenStream;
import axiol.types.*;

import java.io.File;
import java.util.*;

/**
 * The type Language parser.
 */
public class LanguageParser extends Parser {

    private static final EmptyStatement EMPTY_STATEMENT = new EmptyStatement();
    private static final SimpleType NONE = TypeCollection.NONE.toSimpleType();

    private final TokenType[] accessModifier = {
            TokenType.PUBLIC, TokenType.PRIVATE, TokenType.INLINE, TokenType.CONST,
            TokenType.EXTERN, TokenType.PROTECTED
    };

    private final List<Reference> references = new ArrayList<>();
    private ExpressionParser expressionParser;
    private TokenStream tokenStream;
    private String source;
    private String path;

    @Override
    public RootNode parseFile(File file) throws Throwable {
        StringBuilder builder = new StringBuilder();
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine())
            builder.append(scanner.nextLine()).append("\n");

        scanner.close();
        return parseSource(file.toPath().toString(), builder.toString());
    }

    @Override
    public RootNode parseSource(String path, String content) {
        Reference reference = new Reference(ReferenceType.ROOT, "root", TypeCollection.NONE.toSimpleType(),
            UUID.randomUUID(), Accessibility.EXTERN);
        Scope rootScope = new Scope(new ArrayList<>(), null,"root", ScopeElementType.ROOT, reference);

        SourceFile sourceFile = new SourceFile(path, content);
        RootNode rootNode = new RootNode(rootScope, sourceFile);
        LanguageLexer lexer = new LanguageLexer();

        references.clear();

        this.tokenStream = new TokenStream(sourceFile, lexer.tokenizeString(content));
        this.source = content;
        this.path = path;

        this.expressionParser = new ExpressionParser(this, rootScope);

        while (tokenStream.hasMoreTokens()) {
            Statement statement = this.parseStatement(rootScope);

            if (statement != null)
                rootNode.getStatements().add(statement);
        }

        rootNode.getReferences().addAll(references);
        return rootNode;
    }

    /**
     * Parse body statements for global scope.
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
    public Statement parseStatement(Scope parent) {
        if ((isAccessModifier() && isType()) || isType()) {
            if (isAccessModifier()) {
                return this.parseVariableStatement(parent, this.parseAccess());
            }
            return this.parseVariableStatement(parent);
        }
        if ((isAccessModifier() && this.tokenStream.peak(1).getType().equals(TokenType.CLASS)) ||
                this.tokenStream.matches(TokenType.CLASS)) {
            if (isAccessModifier()) {
                return this.parseClassTypeStatement(parent, this.parseAccess());
            }
            return this.parseClassTypeStatement(parent);
        }
        if ((isAccessModifier() && this.tokenStream.peak(1).getType().equals(TokenType.CONSTRUCT)) ||
                this.tokenStream.matches(TokenType.CONSTRUCT)) {
            if (isAccessModifier()) {
                return this.parseConstructStatement(parent, this.parseAccess());
            }
            return this.parseConstructStatement(parent);
        }
        if ((isAccessModifier() && this.tokenStream.peak(1).getType().equals(TokenType.STRUCTURE)) ||
                this.tokenStream.matches(TokenType.STRUCTURE)) {
            if (isAccessModifier()) {
                return this.parseStructStatement(parent, this.parseAccess());
            }
            return this.parseStructStatement(parent);
        }

        if ((isAccessModifier() && this.tokenStream.peak(1).getType().equals(TokenType.FUNCTION))
                || this.tokenStream.matches(TokenType.FUNCTION)) {
            if (isAccessModifier()) {
                return this.parseFunction(parent, this.parseAccess());
            }
            return this.parseFunction(parent);
        }
        if (this.tokenStream.matches(TokenType.LINKED)) {
            this.tokenStream.advance();

            return this.parseLinkingNotice();
        }

        Token unidentified = this.isAccessModifier() ? this.tokenStream.peak(1) : this.tokenStream.current();
        this.createSyntaxError(unidentified, "statement not suited for parsing with token '%s'", unidentified);
        return null;
    }

    public Statement parseConstructStatement(Scope scope, Accessibility... accessibility) {
        TokenPosition position = this.tokenStream.currentPosition();
        this.tokenStream.advance();

        Reference reference = new Reference(ReferenceType.CONSTRUCTOR, "constructor",
                TypeCollection.NONE.toSimpleType(), UUID.randomUUID(), accessibility);
        references.add(reference);

        Scope constructorScope = new Scope(new ArrayList<>(), scope,"constructor", ScopeElementType.CONSTRUCTOR, reference);
        List<Parameter> parameters = this.parseParameters(constructorScope, TokenType.L_PAREN, TokenType.R_PAREN);

        BodyStatement bodyStatement = this.parseBodyStatement(constructorScope);

        scope.getContainingScopes().add(constructorScope);
        return new ConstructStatement(accessibility, parameters, bodyStatement, position);
    }

    private Statement parseClassTypeStatement(Scope scope, Accessibility... accessibility) {
        this.tokenStream.advance();

        this.expected(TokenType.LITERAL);
        String className = this.tokenStream.current().getValue();
        TokenPosition position = this.tokenStream.currentPosition();
        this.tokenStream.advance();

        UUID id = UUID.randomUUID();
        Reference reference = new Reference(ReferenceType.CLASS, className, TypeCollection.NONE.toSimpleType(), id, accessibility);
        Scope classScope = new Scope(new ArrayList<>(), scope, className, ScopeElementType.CLASS, reference);

        String parentClass = null;
        if (this.tokenStream.matches(TokenType.PARENT)) {
            this.tokenStream.advance();

            this.expected(TokenType.LITERAL);
            parentClass = this.tokenStream.current().getValue();
            this.tokenStream.advance();
        }

        BodyStatement bodyStatement = this.parseClassBodyStatement(classScope);

        scope.getContainingScopes().add(classScope);

        this.references.add(reference);
        return new ClassTypeStatement(accessibility, className, parentClass, bodyStatement, id, reference, position);
    }

    private Statement parseStructStatement(Scope scope, Accessibility... accessibility) {
        this.tokenStream.advance();

        if (!this.tokenStream.matches(TokenType.LITERAL)) {
            return null;
        }
        String structName = this.tokenStream.current().getValue();
        TokenPosition position = this.tokenStream.currentPosition();
        this.tokenStream.advance();

        UUID uuid = UUID.randomUUID();
        Reference reference = new Reference(ReferenceType.STRUCT, structName, null, uuid);

        Scope structureScope = new Scope(new ArrayList<>(), scope, structName, ScopeElementType.STRUCT, reference);

        List<Parameter> parameters = this.parseParameters(structureScope, TokenType.L_CURLY, TokenType.R_CURLY);

        scope.getContainingScopes().add(structureScope);
        references.add(reference);
        return new StructTypeStatement(parameters, structName, uuid, accessibility, reference, position);
    }

    public BodyStatement parseClassBodyStatement(Scope scope) {
        if (!this.expected(TokenType.L_CURLY))
            return null;

        Token opening = this.tokenStream.current();
        this.tokenStream.advance();

        List<Statement> statements = new ArrayList<>();
        while (!this.tokenStream.matches(TokenType.R_CURLY)) {
            Statement statement = this.parseStatement(scope);

            if (statement == null)
                continue;

            statements.add(statement);
        }

        this.expected(TokenType.R_CURLY);
        this.tokenStream.advance();

        return new BodyStatement(opening.getTokenPosition(), statements);
    }

    public BodyStatement parseBodyStatement(Scope scope) {
        if (!this.expected(TokenType.L_CURLY))
            return null;

        Token opening = this.tokenStream.current();
        this.tokenStream.advance();

        List<Statement> statements = new ArrayList<>();
        while (!this.tokenStream.matches(TokenType.R_CURLY)) {
            Statement statement = this.parseStatementForBody(scope);

            if (statement == null)
                continue;

            statements.add(statement);
        }

        this.expected(TokenType.R_CURLY);
        this.tokenStream.advance();

        return new BodyStatement(opening.getTokenPosition(), statements);
    }

    /**
     * Parse body statements for scoped areas like functions bodies.
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
    public Statement parseStatementForBody(Scope parent) {
        if (isType()) {
            return this.parseVariableStatement(parent);
        }
        if (isUDTDefinition()) {
            return this.parseUDTDeclare(parent);
        }
        if (this.tokenStream.matches(TokenType.IF)) {
            return this.parseIfStatement(parent);
        }
        if (this.tokenStream.matches(TokenType.WHILE)) {
            return this.parseWhileStatement(parent);
        }
        if (this.tokenStream.matches(TokenType.DO)) {
            return this.parseDoWhileStatement(parent);
        }
        if (this.tokenStream.matches(TokenType.LOOP)) {
            return this.parseLoopStatement(parent);
        }
        if (this.tokenStream.matches(TokenType.FOR)) {
            return this.parseForStatement(parent);
        }
        if (this.tokenStream.matches(TokenType.SWITCH)) {
            return this.parseSwitchStatement(parent);
        }

        // ir or asm modifying statements
        if (this.tokenStream.matches(TokenType.NATIVE)) {
            return this.parseNativeStatement(parent);
        }

        // one line statements
        if (this.tokenStream.matches(TokenType.UNREACHABLE)) {
            return this.parseUnreachable(parent);
        }
        if (this.tokenStream.matches(TokenType.RETURN)) {
            TokenPosition position = this.tokenStream.currentPosition();
            this.tokenStream.advance();

            Expression value = this.parseExpression(parent, NONE);

            expectLineEnd();
            return new ReturnStatement(value, position);
        }
        if (this.tokenStream.matches(TokenType.YIELD)) {
            TokenPosition position = this.tokenStream.currentPosition();
            this.tokenStream.advance();

            Expression value = this.parseExpression(parent, NONE);

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
        Expression expression = this.parseExpression(parent, NONE);
        if (expression != null) {
            if (this.tokenStream.matches(TokenType.SEMICOLON))
                this.tokenStream.advance();

            return expression;
        }

        createSyntaxError("no matching statement found for '%s'", this.tokenStream.current().getType());
        return null;
    }

    private NativeStatement parseNativeStatement(Scope scope) {
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
                    Expression expression = this.parseExpression(scope, NONE);

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

    private SwitchStatement parseSwitchStatement(Scope scope) {
        TokenPosition position = this.tokenStream.currentPosition();
        this.tokenStream.advance();

        Token start = this.tokenStream.current();

        if (!this.expected(TokenType.L_PAREN))
            return null;
        this.tokenStream.advance();

        Expression expression = parseExpression(scope, NONE);

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
                        conditions.add(expressionParser.parseExpression(scope, NONE, 0));

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
                        body = parseBodyStatement(scope);
                    } else {
                        body = parseStatementForBody(scope);
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

    public Statement parseForStatement(Scope scope) {
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
            SimpleType type = this.parseType();

            if (!this.expected(TokenType.LAMBDA))
                return null;
            this.tokenStream.advance();

            Expression expression = this.parseExpression(scope, type);

            if (!this.expected(TokenType.R_PAREN))
                return null;
            this.tokenStream.advance();

            Reference reference = new Reference(ReferenceType.VAR, name, type, UUID.randomUUID());
            this.references.add(reference);
            forCondition = new ForStatement.IterateCondition(reference, expression);
        } else { // for (var; expr; expr)
            Statement start = this.parseVariableStatement(scope, Accessibility.PRIVATE);

            Expression condition = this.parseExpression(scope, NONE);

            if (!this.expected(TokenType.SEMICOLON))
                return null;
            this.tokenStream.advance();

            Expression appliedAction = this.parseExpression(scope, NONE);

            forCondition = new ForStatement.NumberRangeCondition(start, condition, appliedAction);

            if (!this.expected(TokenType.R_PAREN)){
                return null;
            }
            this.tokenStream.advance();
        }

        BodyStatement bodyStatement = this.parseBodyStatement(scope);

        return new ForStatement(forCondition, bodyStatement, position);
    }

    public Statement parseLoopStatement(Scope scope) {
        TokenPosition position = this.tokenStream.currentPosition();
        this.tokenStream.advance();

        BodyStatement bodyStatement = this.parseBodyStatement(scope);

        return new LoopStatement(bodyStatement, position);
    }

    public Statement parseDoWhileStatement(Scope scope) {
        TokenPosition position = this.tokenStream.currentPosition();
        this.tokenStream.advance();

        BodyStatement bodyStatement = this.parseBodyStatement(scope);

        if (!expected(TokenType.WHILE))
            return null;
        this.tokenStream.advance();

        if (!this.expected(TokenType.L_PAREN))
            return null;
        this.tokenStream.advance();

        Expression condition = this.parseExpression(scope, NONE);

        if (!this.expected(TokenType.R_PAREN))
            return null;
        this.tokenStream.advance();

        if (!this.expected(TokenType.SEMICOLON))
            return null;
        this.tokenStream.advance();

        return new DoWhileStatement(condition, bodyStatement, position);
    }

    public Statement parseWhileStatement(Scope scope) {
        TokenPosition position = this.tokenStream.currentPosition();
        this.tokenStream.advance();

        if (!this.expected(TokenType.L_PAREN))
            return null;
        this.tokenStream.advance();

        Expression condition = this.parseExpression(scope, NONE);

        if (!this.expected(TokenType.R_PAREN))
            return null;
        this.tokenStream.advance();

        BodyStatement bodyStatement = this.parseBodyStatement(scope);

        return new WhileStatement(condition, bodyStatement, position);
    }

    public Statement parseUnreachable(Scope scope) {
        TokenPosition position = this.tokenStream.currentPosition();
        this.tokenStream.advance();

        if (!this.expected(TokenType.SEMICOLON))
            return null;
        this.tokenStream.advance();

        return new UnreachableStatement(position);
    }

    public Statement parseIfStatement(Scope scope) {
        this.tokenStream.advance();

        if (!this.expected(TokenType.L_PAREN))
            return null;
        this.tokenStream.advance();

        TokenPosition position = this.tokenStream.currentPosition();
        Expression condition = this.parseExpression(scope, NONE);

        if (!this.expected(TokenType.R_PAREN))
            return null;
        this.tokenStream.advance();

        BodyStatement bodyStatement = this.parseBodyStatement(scope);

        if (this.tokenStream.matches(TokenType.ELSE)) {
            this.tokenStream.advance();

            Statement elseStatement = null;

            if (this.tokenStream.matches(TokenType.L_CURLY)) {
                elseStatement = this.parseBodyStatement(scope);
            }
            if (this.tokenStream.matches(TokenType.IF)) {
                elseStatement = this.parseIfStatement(scope); // loop
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

    public Statement parseFunction(Scope scope, Accessibility... accessibility) {
        this.tokenStream.advance();

        if (!this.tokenStream.matches(TokenType.LITERAL)) {
            return null;
        }
        String functionName = this.tokenStream.current().getValue();
        TokenPosition position = this.tokenStream.currentPosition();

        this.tokenStream.advance();

        UUID id = UUID.randomUUID();
        Reference reference = new Reference(ReferenceType.FUNCTION, functionName, null, id, accessibility);
        this.references.add(reference);
        Scope functionScope = new Scope(new ArrayList<>(), scope, functionName, ScopeElementType.FUNCTION, reference);

        List<Parameter> parameters = this.parseParameters(functionScope, TokenType.L_PAREN, TokenType.R_PAREN);

        SimpleType returnType = new SimpleType(TypeCollection.VOID, 0, 0);
        if (this.tokenStream.matches(TokenType.LAMBDA)) {
            this.tokenStream.advance();

            returnType = this.parseType();
        }
        reference.setValueType(returnType);

        BodyStatement bodyStatement = this.parseBodyStatement(functionScope);
        scope.getContainingScopes().add(functionScope);

        return new FunctionStatement(functionName, accessibility,
                        parameters, bodyStatement, returnType, id, reference, position);
    }

    public List<Parameter> parseParameters(Scope scope, TokenType open, TokenType close) {
        List<Parameter> parameters = new ArrayList<>();

        if (!this.tokenStream.matches(open)) {
            return null;
        }
        this.tokenStream.advance();

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

            SimpleType type = this.parseType();

            // todo change this mby
            Reference reference = new Reference(ReferenceType.PARAMETER, parameterName, type, UUID.randomUUID());
            ScopeElement scopeEntry = new ScopeElement(parameterName, ScopeElementType.PARAM, reference);
            scope.getContainingScopes().add(scopeEntry);

            this.references.add(reference);

            if (this.tokenStream.matches(TokenType.COMMA)) {
                this.tokenStream.advance();

                parameters.add(new Parameter(parameterName, type, null, pointer, referenced, reference));
                continue;
            }
            if (this.tokenStream.matches(TokenType.EQUAL)) {
                this.tokenStream.advance();

                Expression defaultValue = this.expressionParser.parseExpression(scope, type,0);
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

    public Statement parseUDTDeclare(Scope scope) {
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
            Expression expression = this.parseExpression(scope, NONE);

            if (expression != null)
                parameters.add(expression);

            if (this.tokenStream.matches(TokenType.COMMA))
                this.tokenStream.advance();
        }
        this.expected(TokenType.R_PAREN);
        this.tokenStream.advance();

        if (this.tokenStream.matches(TokenType.SEMICOLON))
            this.tokenStream.advance();

        UUID uuid = UUID.randomUUID();
        Reference reference = new Reference(ReferenceType.UDT, udtName,
                PrimitiveTypes.U0.toType().toSimpleType(), uuid);

        return new UDTDeclareStatement(udtType, udtName, parameters, reference, position);
    }

    public Statement parseVariableStatement(Scope scope, Accessibility... accessibility) {
        SimpleType type = this.parseType();

        if (!expected(TokenType.LITERAL))
            return null;

        String name = this.tokenStream.current().getValue();

        TokenPosition position = this.tokenStream.currentPosition();
        this.tokenStream.advance();

        Expression initExpression = null;
        if (this.tokenStream.matches(TokenType.EQUAL)) {
            this.tokenStream.advance();

            initExpression = this.parseExpression(scope, type);
        }

        if (this.tokenStream.matches(TokenType.SEMICOLON))
            this.tokenStream.advance();

        UUID id = UUID.randomUUID();


        Reference reference = new Reference(ReferenceType.VAR, name, type, id, accessibility);
        this.references.add(reference);

        scope.getContainingScopes().add(new ScopeElement(name, ScopeElementType.VAR, reference));

        return new VariableStatement(name, type, initExpression, id, reference, position, accessibility);
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

        return !TypeCollection.typeByToken(this.tokenStream.peak(peak)).equals(TypeCollection.NONE);
    }

    public boolean isAccessModifier() {
        return Arrays.stream(this.accessModifier).anyMatch(type -> type == this.tokenStream.current().getType());
    }

    public SimpleType parseType() {
        int pointerDepth = 0;
        if (this.tokenStream.matches(TokenType.MULTIPLY)) {
            while (this.tokenStream.matches(TokenType.MULTIPLY)) {
                this.tokenStream.advance();
                pointerDepth++;
            }
        }

        Token typeToken = this.tokenStream.current();
        Type type = TypeCollection.typeByToken(typeToken);
        this.tokenStream.advance();

        int arrayDepth = 0;
        while (this.tokenStream.matches(TokenType.L_SQUARE)) {
            this.tokenStream.advance();

            this.expected(TokenType.R_SQUARE);
            this.tokenStream.advance();
            arrayDepth++;
        }

        return new SimpleType(type, arrayDepth, pointerDepth);
    }

    public void expectLineEnd() {
        if (this.expected(TokenType.SEMICOLON))
            this.tokenStream.advance();
    }

    @Override
    public Expression parseExpression(Scope scope, SimpleType simpleType) {
        return this.expressionParser.parseExpression(scope, simpleType, Operator.MAX_PRIORITY);
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

    public List<Reference> getReferences() {
        return references;
    }

    public String getPath() {
        return path;
    }

    public ExpressionParser getExpressionParser() {
        return expressionParser;
    }

    public String getSource() {
        return source;
    }

    public TokenType[] getAccessModifier() {
        return accessModifier;
    }
}
