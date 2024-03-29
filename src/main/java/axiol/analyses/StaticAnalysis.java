package axiol.analyses;

import axiol.mangler.Mangler;
import axiol.parser.RootNodeProcessor;
import axiol.parser.statement.Parameter;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.RootNode;
import axiol.parser.tree.Statement;
import axiol.parser.tree.expressions.*;
import axiol.parser.tree.expressions.control.MatchExpression;
import axiol.parser.tree.expressions.extra.ElementReferenceExpression;
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
import axiol.types.ScopeVariable;
import axiol.types.custom.I128;
import axiol.types.custom.U128;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class StaticAnalysis implements RootNodeProcessor<Void> {

    private final Mangler mangler = new Mangler();
    private final AnalyseContext analyseContext = new AnalyseContext();

    private final NumberRangeCheck I8_Check   = new NumberRangeCheck(0x7F,                -0x80);
    private final NumberRangeCheck I16_Check  = new NumberRangeCheck(0x7FFF,              -0x8000);
    private final NumberRangeCheck I32_Check  = new NumberRangeCheck(0x7FFFFFFF,          -0x80000000);
    private final NumberRangeCheck I64_Check  = new NumberRangeCheck(0x7fffffffffffffffL, -0x8000000000000000L);
    private final NumberRangeCheck I128_Check = new NumberRangeCheck(I128.MAX_VALUE, I128.MIN_VALUE);

    private final NumberRangeCheck U8_Check   = new NumberRangeCheck(0xFF,                0x00);
    private final NumberRangeCheck U16_Check  = new NumberRangeCheck(0xFFFF,              0x0000);
    private final NumberRangeCheck U32_Check  = new NumberRangeCheck(0xFFFFFFFFL,         0x00000000);
    private final NumberRangeCheck U64_Check  = new NumberRangeCheck(0xFFFFFFFFFFFFFFFFL, 0x0000000000000000);
    private final NumberRangeCheck U128_Check = new NumberRangeCheck(U128.MAX_VALUE, U128.MIN_VALUE);

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
            NodeType.MATCH_EXPR, NodeType.ELEMENT_REFERENCE_EXPR, NodeType.BOOLEAN_EXPR,
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
            this.processStatement(sourceFile, "", "global", new ArrayList<>(), statement);
        }
        return rootNode;
    }

    @Override
    public Void processNewReturn(RootNode rootNode) {
        throw new IllegalArgumentException("not implemented!");
    }

    //@formatter:off
    public void processStatement(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, Statement statement) {
        if (Arrays.stream(definitionTypes).anyMatch(aClass -> aClass == statement.type())) {
            this.processDefinitionStatements(sourceFile, scope, scopeMangled, scopeVars, statement);
            return;
        }
        if (Arrays.stream(declarationTypes).anyMatch(aClass -> aClass == statement.type())) {
            this.processDeclarationStatements(sourceFile, scope, scopeMangled, scopeVars, statement);
            return;
        }
        if (Arrays.stream(controlFlowTypes).anyMatch(aClass -> aClass == statement.type())) {
            this.processControlFlowStatements(sourceFile, scope, scopeMangled, scopeVars, statement);
            return;
        }
        if (Arrays.stream(expressionTypes).anyMatch(aClass -> aClass == statement.type())) {
            this.processExpressions(sourceFile, scope, scopeMangled, scopeVars, statement);
            return;
        }
        if (Arrays.stream(specialCaseTypes).anyMatch(aClass -> aClass == statement.type())) {
            this.processSpecialCasesStatements(sourceFile, scope, scopeMangled, scopeVars, statement);
            return;
        }

        ValidationException.UNMATCHED_STATEMENT
                .throwException(sourceFile, statement.position(),
                        statement.type(), "General Processing");
    }

    public void processDefinitionStatements(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, Statement statement) {
        switch (statement.type()) {
            case CLASS_TYPE_STATEMENT ->  this.analyseClassType(    sourceFile, scope, scopeMangled, scopeVars, (ClassTypeStatement)    statement);
            case FUNCTION_STATEMENT ->    this.analyseFunction(     sourceFile, scope, scopeMangled, scopeVars, (FunctionStatement)     statement);
            case STRUCT_TYPE_STATEMENT -> this.analyseStructureType(sourceFile, scope, scopeMangled, scopeVars, (StructTypeStatement)   statement);
            case CONSTRUCT_STATEMENT ->   this.analyseConstruct(    sourceFile, scope, scopeMangled, scopeVars, (ConstructStatement)    statement);
            case VAR_STATEMENT ->         this.analyseVariable(     sourceFile, scope, scopeMangled, scopeVars, (VariableStatement)     statement);
            case LINKED_STATEMENT ->      this.analyseLinkedNotice( sourceFile, scope, scopeMangled, scopeVars, (LinkedNoticeStatement) statement);

            default -> ValidationException.UNMATCHED_STATEMENT
                    .throwException(sourceFile, statement.position(),
                            statement.type(), "Definition");
        }
    }

    public void processDeclarationStatements(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, Statement statement) {
        if (statement instanceof UDTDeclareStatement udtDeclareStatement) {
            this.analyseUDTDeclaration(sourceFile, scope, scopeMangled, scopeVars, udtDeclareStatement);
        }
    }

    public void processControlFlowStatements(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, Statement statement) {
        switch (statement.type()) {
            case BREAK_STATEMENT ->       this.analyseBreak(      sourceFile, scope, scopeMangled, scopeVars, (BreakStatement)       statement);
            case FOR_STATEMENT ->         this.analyseFor(        sourceFile, scope, scopeMangled, scopeVars, (ForStatement)         statement);
            case CONTINUE_STATEMENT ->    this.analyseContinue(   sourceFile, scope, scopeMangled, scopeVars, (ContinueStatement)    statement);
            case DO_WHILE_STATEMENT ->    this.analyseDoWhile(    sourceFile, scope, scopeMangled, scopeVars, (DoWhileStatement)     statement);
            case IF_STATEMENT ->          this.analyseIf(         sourceFile, scope, scopeMangled, scopeVars, (IfStatement)          statement);
            case LOOP_STATEMENT ->        this.analyseLoop(       sourceFile, scope, scopeMangled, scopeVars, (LoopStatement)        statement);
            case RETURN_STATEMENT ->      this.analyseReturn(     sourceFile, scope, scopeMangled, scopeVars, (ReturnStatement)      statement);
            case SWITCH_STATEMENT ->      this.analyseSwitch(     sourceFile, scope, scopeMangled, scopeVars, (SwitchStatement)      statement);
            case UNREACHABLE_STATEMENT -> this.analyseUnreachable(sourceFile, scope, scopeMangled, scopeVars, (UnreachableStatement) statement);
            case WHILE_STATEMENT ->       this.analyseWhile(      sourceFile, scope, scopeMangled, scopeVars, (WhileStatement)       statement);
            case YIELD_STATEMENT ->       this.analyseYield(      sourceFile, scope, scopeMangled, scopeVars, (YieldStatement)       statement);

            default -> ValidationException.UNMATCHED_STATEMENT
                    .throwException(sourceFile, statement.position(),
                            statement.type(), "ControlFlow");
        }
    }

    public void processSpecialCasesStatements(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, Statement statement) {
        switch (statement.type()) {
            case BODY_STATEMENT ->        this.analyseBody(  sourceFile, scope, scopeMangled, scopeVars, (BodyStatement)   statement);
            case NATIVE_STATEMENT ->      this.analyseNative(sourceFile, scope, scopeMangled, scopeVars, (NativeStatement) statement);

            default -> ValidationException.UNMATCHED_STATEMENT
                    .throwException(sourceFile, statement.position(),
                            statement.type(), "SpecialCase");
        }
    }

    public void processExpressions(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, Statement statement) {
        switch (statement.type()) {
            case MATCH_EXPR ->            this.analyseMatch(    sourceFile, scope, scopeMangled, scopeVars, (MatchExpression)     statement);
            case ELEMENT_REFERENCE_EXPR ->this.analyseReference(sourceFile, scope, scopeMangled, scopeVars, (ElementReferenceExpression) statement);
            case BOOLEAN_EXPR ->          this.analyseBoolean(  sourceFile, scope, scopeMangled, scopeVars, (BooleanExpression)   statement);
            case NUMBER_EXPR ->           this.analyseNumber(   sourceFile, scope, scopeMangled, scopeVars, (NumberExpression)    statement);
            case STRING_EXPR ->           this.analyseString(   sourceFile, scope, scopeMangled, scopeVars, (StringExpression)    statement);
            case ARRAY_EXPR ->            this.analyseArray(    sourceFile, scope, scopeMangled, scopeVars, (ArrayInitExpression) statement);
            case BINARY_EXPR ->           this.analyseBinary(   sourceFile, scope, scopeMangled, scopeVars, (BinaryExpression)    statement);
            case UNARY_EXPR ->            this.analyseUnary(    sourceFile, scope, scopeMangled, scopeVars, (UnaryExpression)     statement);
            case LITERAL_EXPR ->          this.analyseLiteral(  sourceFile, scope, scopeMangled, scopeVars, (LiteralExpression)   statement);
            case CALL_EXPR ->             this.analyseCall(     sourceFile, scope, scopeMangled, scopeVars, (CallExpression)      statement);

            default -> ValidationException.UNMATCHED_STATEMENT
                    .throwException(sourceFile, statement.position(),
                            statement.type(), "Expressions");
        }
    }
    //@formatter:on

    private void analyseClassType(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, ClassTypeStatement statement) {
    }

    private void analyseFunction(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, FunctionStatement statement) {
    }

    private void analyseStructureType(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, StructTypeStatement statement) {
    }

    private void analyseConstruct(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, ConstructStatement statement) {
    }

    private void analyseVariable(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, VariableStatement statement) {

    }

    private void analyseLinkedNotice(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, LinkedNoticeStatement statement) {
    }

    private void analyseUDTDeclaration(SourceFile sourceFile, String scope, String scopeMangled,
                                       List<ScopeVariable> scopeVars, UDTDeclareStatement udtDeclareStatement) {

    }

    private void analyseBreak(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, BreakStatement statement) {
    }

    private void analyseContinue(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, ContinueStatement statement) {
    }

    private void analyseFor(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, ForStatement statement) {
    }

    private void analyseDoWhile(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, DoWhileStatement statement) {
    }

    private void analyseIf(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, IfStatement statement) {
    }

    private void analyseLoop(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, LoopStatement statement) {
    }

    private void analyseReturn(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, ReturnStatement statement) {
    }

    private void analyseSwitch(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, SwitchStatement statement) {
    }

    private void analyseUnreachable(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, UnreachableStatement statement) {
    }

    private void analyseWhile(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, WhileStatement statement) {
    }

    private void analyseYield(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, YieldStatement statement) {
    }

    private void analyseBody(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, BodyStatement statement) {
    }

    private void analyseNative(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, NativeStatement statement) {
    }

    private void analyseMatch(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, MatchExpression statement) {
    }

    private void analyseReference(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, ElementReferenceExpression statement) {
    }

    private void analyseBoolean(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, BooleanExpression statement) {
    }

    private void analyseNumber(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, NumberExpression statement) {
    }

    private void analyseString(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, StringExpression statement) {
    }

    private void analyseArray(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, ArrayInitExpression statement) {
    }

    private void analyseBinary(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, BinaryExpression statement) {
    }

    private void analyseUnary(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, UnaryExpression statement) {
    }

    private void analyseLiteral(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, LiteralExpression statement) {

    }

    private void analyseCall(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, CallExpression statement) {

    }

}
