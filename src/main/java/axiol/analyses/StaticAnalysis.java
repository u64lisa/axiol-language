package axiol.analyses;

import axiol.mangler.Mangler;
import axiol.parser.RootNodeProcessor;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.RootNode;
import axiol.parser.tree.Statement;
import axiol.parser.tree.expressions.*;
import axiol.parser.tree.expressions.control.MatchExpression;
import axiol.parser.tree.expressions.extra.ReferenceExpression;
import axiol.parser.tree.expressions.sub.BooleanExpression;
import axiol.parser.tree.expressions.sub.NumberExpression;
import axiol.parser.tree.expressions.sub.StringExpression;
import axiol.parser.tree.statements.BodyStatement;
import axiol.parser.tree.statements.LinkedNoticeStatement;
import axiol.parser.tree.statements.VariableStatement;
import axiol.parser.tree.statements.control.*;
import axiol.parser.tree.statements.oop.*;
import axiol.parser.tree.statements.special.NativeStatement;
import axiol.parser.util.SourceFile;
import axiol.parser.util.error.LanguageException;
import axiol.parser.util.error.TokenPosition;

import java.util.Arrays;

public class StaticAnalysis implements RootNodeProcessor {

    private final Mangler mangler = new Mangler();
    private final AnalyseContext analyseContext = new AnalyseContext();

    private static final String NAME_PATTERN = "sc-%s_na-%s";

    private final NodeType[] definitionTypes = {
            NodeType.CLASS_TYPE_STATEMENT, NodeType.FUNCTION_STATEMENT,
            NodeType.STRUCT_TYPE_STATEMENT, NodeType.CONSTRUCT_STATEMENT,
            NodeType.VAR_STATEMENT, NodeType.LINKED_STATEMENT,
    };
    private final NodeType[] declarationTypes = {
            NodeType.UDT_DECLARE_STATEMENT
    };
    private final NodeType[] controlFlowTypes = {
            NodeType.BREAK_STATEMENT, NodeType.CONTINUE_STATEMENT, NodeType.DO_WHILE_STATEMENT,
            NodeType.FOR_STATEMENT, NodeType.IF_STATEMENT, NodeType.LOOP_STATEMENT,
            NodeType.RETURN_STATEMENT, NodeType.SWITCH_STATEMENT, NodeType.UNREACHABLE_STATEMENT,
            NodeType.WHILE_STATEMENT, NodeType.YIELD_STATEMENT
    };
    private final NodeType[] expressionTypes = {
            NodeType.MATCH_EXPR, NodeType.REFERENCE_EXPR, NodeType.BOOLEAN_EXPR,
            NodeType.NUMBER_EXPR, NodeType.STRING_EXPR, NodeType.ARRAY_EXPR,
            NodeType.BINARY_EXPR, NodeType.UNARY_EXPR, NodeType.LITERAL_EXPR,
            NodeType.CALL_EXPR
    };
    private final NodeType[] specialCaseTypes = {
            NodeType.NATIVE_STATEMENT, NodeType.BODY_STATEMENT
    };

    @Override
    public RootNode process(RootNode rootNode) {
        SourceFile sourceFile = rootNode.getSourceFile();
        for (Statement statement : rootNode.getStatements()) {
            this.processStatement(sourceFile, "global", statement);
        }
        return rootNode;
    }

    public void processStatement(SourceFile sourceFile, String scope, Statement statement) {
        if (Arrays.stream(definitionTypes).anyMatch(aClass -> aClass == statement.type())) {
            this.processDefinitionStatements(sourceFile, scope, statement);
            return;
        }
        if (Arrays.stream(declarationTypes).anyMatch(aClass -> aClass == statement.type())) {
            this.processDeclarationStatements(sourceFile, scope, statement);
            return;
        }
        if (Arrays.stream(controlFlowTypes).anyMatch(aClass -> aClass == statement.type())) {
            this.processControlFlowStatements(sourceFile, scope, statement);
            return;
        }
        if (Arrays.stream(expressionTypes).anyMatch(aClass -> aClass == statement.type())) {
            this.processExpressions(sourceFile, statement);
            return;
        }
        if (Arrays.stream(specialCaseTypes).anyMatch(aClass -> aClass == statement.type())) {
            this.processSpecialCasesStatements(sourceFile, scope, statement);
            return;
        }

        ValidationException.UNMATCHED_STATEMENT
                .throwException(sourceFile, statement.position(),
                        statement.type(), "General Processing");
    }

    public void processDefinitionStatements(SourceFile sourceFile, String scope, Statement statement) {
        switch (statement.type()) {
            case CLASS_TYPE_STATEMENT ->  this.analyseClassType(sourceFile, scope, (ClassTypeStatement) statement);
            case FUNCTION_STATEMENT ->    this.analyseFunction(sourceFile, scope, (FunctionStatement) statement);
            case STRUCT_TYPE_STATEMENT -> this.analyseStructureType(sourceFile,scope, (StructTypeStatement) statement);
            case CONSTRUCT_STATEMENT ->   this.analyseConstruct(sourceFile, scope, (ConstructStatement) statement);
            case VAR_STATEMENT ->         this.analyseVariable(sourceFile, scope, (VariableStatement) statement);
            case LINKED_STATEMENT ->      this.analyseLinkedNotice(sourceFile, scope, (LinkedNoticeStatement) statement);

            default -> ValidationException.UNMATCHED_STATEMENT
                    .throwException(sourceFile, statement.position(),
                            statement.type(), "Definition");
        }
    }

    public void processDeclarationStatements(SourceFile sourceFile, String scope, Statement statement) {
        if (statement instanceof UDTDeclareStatement udtDeclareStatement) {
            this.analyseUDTDeclaration(sourceFile, scope, udtDeclareStatement);
        }
    }

    public void processControlFlowStatements(SourceFile sourceFile, String scope, Statement statement) {
        switch (statement.type()) {
            case BREAK_STATEMENT ->       this.analyseBreak(sourceFile, scope, (BreakStatement) statement);
            case FOR_STATEMENT ->         this.analyseFor(sourceFile, scope, (ForStatement) statement);
            case CONTINUE_STATEMENT ->    this.analyseContinue(sourceFile, scope, (ContinueStatement) statement);
            case DO_WHILE_STATEMENT ->    this.analyseDoWhile(sourceFile, scope, (DoWhileStatement) statement);
            case IF_STATEMENT ->          this.analyseIf(sourceFile, scope, (IfStatement) statement);
            case LOOP_STATEMENT ->        this.analyseLoop(sourceFile, scope, (LoopStatement) statement);
            case RETURN_STATEMENT ->      this.analyseReturn(sourceFile, scope, (ReturnStatement) statement);
            case SWITCH_STATEMENT ->      this.analyseSwitch(sourceFile, scope, (SwitchStatement) statement);
            case UNREACHABLE_STATEMENT -> this.analyseUnreachable(sourceFile, scope, (UnreachableStatement) statement);
            case WHILE_STATEMENT ->       this.analyseWhile(sourceFile, scope, (WhileStatement) statement);
            case YIELD_STATEMENT ->       this.analyseYield(sourceFile, scope, (YieldStatement) statement);

            default -> ValidationException.UNMATCHED_STATEMENT
                    .throwException(sourceFile, statement.position(),
                            statement.type(), "ControlFlow");
        }
    }

    public void processSpecialCasesStatements(SourceFile sourceFile, String scope, Statement statement) {
        switch (statement.type()) {
            case BODY_STATEMENT ->        this.analyseBody(sourceFile, scope, (BodyStatement) statement);
            case NATIVE_STATEMENT ->      this.analyseNative(sourceFile, scope, (NativeStatement) statement);

            default -> ValidationException.UNMATCHED_STATEMENT
                    .throwException(sourceFile, statement.position(),
                            statement.type(), "SpecialCase");
        }
    }

    public void processExpressions(SourceFile sourceFile, Statement statement) {
        switch (statement.type()) {
            case MATCH_EXPR ->            this.analyseMatch(sourceFile, (MatchExpression) statement);
            case REFERENCE_EXPR ->        this.analyseReference(sourceFile, (ReferenceExpression) statement);
            case BOOLEAN_EXPR ->          this.analyseBoolean(sourceFile, (BooleanExpression) statement);
            case NUMBER_EXPR ->           this.analyseNumber(sourceFile, (NumberExpression) statement);
            case STRING_EXPR ->           this.analyseString(sourceFile, (StringExpression) statement);
            case ARRAY_EXPR ->            this.analyseArray(sourceFile, (ArrayInitExpression) statement);
            case BINARY_EXPR ->           this.analyseBinary(sourceFile, (BinaryExpression) statement);
            case UNARY_EXPR ->            this.analyseUnary(sourceFile, (UnaryExpression) statement);
            case LITERAL_EXPR ->          this.analyseLiteral(sourceFile, (LiteralExpression) statement);
            case CALL_EXPR ->             this.analyseCall(sourceFile, (CallExpression) statement);

            default -> ValidationException.UNMATCHED_STATEMENT
                    .throwException(sourceFile, statement.position(),
                            statement.type(), "Expressions");
        }
    }

    private void analyseClassType(SourceFile sourceFile, String scope, ClassTypeStatement statement) {
        String mangel = this.mangler.mangelClass(scope, statement);
        if (this.analyseContext.checkMangel(mangel)) {

            for (Statement statements : statement.getBodyStatement().getStatements()) {
                this.processStatement(sourceFile,mangel, statements);
            }
            return;
        }
        this.createSyntaxError(sourceFile, statement.position(), "duplicated class found: '%s'", statement.getName());
    }

    private void analyseFunction(SourceFile sourceFile, String scope, FunctionStatement statement) {
        String mangel = this.mangler.mangelFunction(scope, statement);
        if (this.analyseContext.checkMangel(mangel)) {

            for (Statement statements : statement.getBodyStatement().getStatements()) {
                this.processStatement(sourceFile,mangel, statements);
            }
            return;
        }
        this.createSyntaxError(sourceFile, statement.position(), "duplicated function found: '%s'", statement.getName());
    }

    private void analyseStructureType(SourceFile sourceFile, String scope, StructTypeStatement statement) {
        String mangel = this.mangler.mangelStruct(scope, statement);
        if (this.analyseContext.checkMangel(mangel)) {

            return;
        }
        this.createSyntaxError(sourceFile, statement.position(), "duplicated function found: '%s'", statement.getName());
    }

    private void analyseConstruct(SourceFile sourceFile, String scope, ConstructStatement statement) {
        String mangel = this.mangler.mangelConstruct(scope, statement);
        if (this.analyseContext.checkMangel(mangel)) {

            return;
        }
        this.createSyntaxError(sourceFile, statement.position(), "duplicated construct found from class '%s'", scope);
    }

    private void analyseVariable(SourceFile sourceFile, String scope, VariableStatement statement) {
        String mangel = this.mangler.mangelVariable(scope, statement);
        if (this.analyseContext.checkMangel(mangel)) {
            // todo check for functions parameters so there is no overloading and/or matching types
            return;
        }
        this.createSyntaxError(sourceFile, statement.position(), "duplicated variable found: '%s'", statement.getName());
    }

    private void analyseLinkedNotice(SourceFile sourceFile, String scope, LinkedNoticeStatement statement) {
    }

    private void analyseUDTDeclaration(SourceFile sourceFile, String scope, UDTDeclareStatement udtDeclareStatement) {
    }

    private void analyseBreak(SourceFile sourceFile, String scope, BreakStatement statement) {
    }

    private void analyseContinue(SourceFile sourceFile, String scope, ContinueStatement statement) {
    }

    private void analyseFor(SourceFile sourceFile, String scope, ForStatement statement) {
    }

    private void analyseDoWhile(SourceFile sourceFile, String scope, DoWhileStatement statement) {
    }

    private void analyseIf(SourceFile sourceFile, String scope, IfStatement statement) {
    }

    private void analyseLoop(SourceFile sourceFile, String scope, LoopStatement statement) {
    }

    private void analyseReturn(SourceFile sourceFile, String scope, ReturnStatement statement) {
    }

    private void analyseSwitch(SourceFile sourceFile, String scope, SwitchStatement statement) {
    }

    private void analyseUnreachable(SourceFile sourceFile, String scope, UnreachableStatement statement) {
    }

    private void analyseWhile(SourceFile sourceFile, String scope, WhileStatement statement) {
    }

    private void analyseYield(SourceFile sourceFile, String scope, YieldStatement statement) {
    }

    private void analyseBody(SourceFile sourceFile, String scope, BodyStatement statement) {
    }

    private void analyseNative(SourceFile sourceFile, String scope, NativeStatement statement) {
    }

    private void analyseMatch(SourceFile sourceFile, MatchExpression statement) {
    }

    private void analyseReference(SourceFile sourceFile, ReferenceExpression statement) {
    }

    private void analyseBoolean(SourceFile sourceFile, BooleanExpression statement) {
    }

    private void analyseNumber(SourceFile sourceFile, NumberExpression statement) {
    }

    private void analyseString(SourceFile sourceFile, StringExpression statement) {
    }

    private void analyseArray(SourceFile sourceFile, ArrayInitExpression statement) {
    }

    private void analyseBinary(SourceFile sourceFile, BinaryExpression statement) {
    }

    private void analyseUnary(SourceFile sourceFile, UnaryExpression statement) {
    }

    private void analyseLiteral(SourceFile sourceFile, LiteralExpression statement) {
    }

    private void analyseCall(SourceFile sourceFile, CallExpression statement) {
    }

    public void createSyntaxError(SourceFile sourceFile, TokenPosition position, String message, Object... args) {
        LanguageException languageException = new LanguageException(sourceFile.getContent(),
                position, sourceFile.getFilePath(), message, args);
        languageException.throwError();
    }

}
