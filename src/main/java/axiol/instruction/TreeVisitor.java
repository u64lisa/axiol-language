package axiol.instruction;

import axiol.types.ScopeVariable;
import axiol.analyses.ValidationException;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class TreeVisitor<T> implements RootNodeProcessor<T> {

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
            this.processStatement(sourceFile, "", new ArrayList<>(), statement);
        }
        
        return rootNode;
    }

    @Override
    public T processNewReturn(RootNode rootNode) {
        this.process(rootNode);
        return null;
    }

    //@formatter:off
    public void processStatement(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, Statement statement) {
        if (Arrays.stream(definitionTypes).anyMatch(aClass -> aClass == statement.type())) {
            this.processDefinitionStatements(sourceFile, scope, scopeVars, statement);
            return;
        }
        if (Arrays.stream(declarationTypes).anyMatch(aClass -> aClass == statement.type())) {
            this.processDeclarationStatements(sourceFile, scope, scopeVars, statement);
            return;
        }
        if (Arrays.stream(controlFlowTypes).anyMatch(aClass -> aClass == statement.type())) {
            this.processControlFlowStatements(sourceFile, scope, scopeVars, statement);
            return;
        }
        if (Arrays.stream(expressionTypes).anyMatch(aClass -> aClass == statement.type())) {
            this.processExpressions(sourceFile, scope, scopeVars, statement);
            return;
        }
        if (Arrays.stream(specialCaseTypes).anyMatch(aClass -> aClass == statement.type())) {
            this.processSpecialCasesStatements(sourceFile, scope, scopeVars, statement);
            return;
        }

        ValidationException.UNMATCHED_STATEMENT
                .throwException(sourceFile, statement.position(),
                        statement.type(), "General Processing");
    }

    public void processDefinitionStatements(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, Statement statement) {
        switch (statement.type()) {
            case CLASS_TYPE_STATEMENT ->  this.visitClassType(    sourceFile, scope, scopeVars, (ClassTypeStatement)    statement);
            case FUNCTION_STATEMENT ->    this.visitFunction(     sourceFile, scope, scopeVars, (FunctionStatement)     statement);
            case STRUCT_TYPE_STATEMENT -> this.visitStructureType(sourceFile, scope, scopeVars, (StructTypeStatement)   statement);
            case CONSTRUCT_STATEMENT ->   this.visitConstruct(    sourceFile, scope, scopeVars, (ConstructStatement)    statement);
            case VAR_STATEMENT ->         this.visitVariable(     sourceFile, scope, scopeVars, (VariableStatement)     statement);
            case LINKED_STATEMENT ->      this.visitLinkedNotice( sourceFile, scope, scopeVars, (LinkedNoticeStatement) statement);

            default -> ValidationException.UNMATCHED_STATEMENT
                    .throwException(sourceFile, statement.position(),
                            statement.type(), "Definition");
        }
    }

    public void processDeclarationStatements(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, Statement statement) {
        if (statement instanceof UDTDeclareStatement udtDeclareStatement) {
            this.visitUDTDeclaration(sourceFile, scope, scopeVars, udtDeclareStatement);
        }
    }

    public void processControlFlowStatements(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, Statement statement) {
        switch (statement.type()) {
            case BREAK_STATEMENT ->       this.visitBreak(      sourceFile, scope, scopeVars, (BreakStatement)       statement);
            case FOR_STATEMENT ->         this.visitFor(        sourceFile, scope, scopeVars, (ForStatement)         statement);
            case CONTINUE_STATEMENT ->    this.visitContinue(   sourceFile, scope, scopeVars, (ContinueStatement)    statement);
            case DO_WHILE_STATEMENT ->    this.visitDoWhile(    sourceFile, scope, scopeVars, (DoWhileStatement)     statement);
            case IF_STATEMENT ->          this.visitIf(         sourceFile, scope, scopeVars, (IfStatement)          statement);
            case LOOP_STATEMENT ->        this.visitLoop(       sourceFile, scope, scopeVars, (LoopStatement)        statement);
            case RETURN_STATEMENT ->      this.visitReturn(     sourceFile, scope, scopeVars, (ReturnStatement)      statement);
            case SWITCH_STATEMENT ->      this.visitSwitch(     sourceFile, scope, scopeVars, (SwitchStatement)      statement);
            case UNREACHABLE_STATEMENT -> this.visitUnreachable(sourceFile, scope, scopeVars, (UnreachableStatement) statement);
            case WHILE_STATEMENT ->       this.visitWhile(      sourceFile, scope, scopeVars, (WhileStatement)       statement);
            case YIELD_STATEMENT ->       this.visitYield(      sourceFile, scope, scopeVars, (YieldStatement)       statement);

            default -> ValidationException.UNMATCHED_STATEMENT
                    .throwException(sourceFile, statement.position(),
                            statement.type(), "ControlFlow");
        }
    }

    public void processSpecialCasesStatements(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, Statement statement) {
        switch (statement.type()) {
            case BODY_STATEMENT ->        this.visitBody(  sourceFile, scope, scopeVars, (BodyStatement)   statement);
            case NATIVE_STATEMENT ->      this.visitNative(sourceFile, scope, scopeVars, (NativeStatement) statement);

            default -> ValidationException.UNMATCHED_STATEMENT
                    .throwException(sourceFile, statement.position(),
                            statement.type(), "SpecialCase");
        }
    }

    public void processExpressions(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, Statement statement) {
        switch (statement.type()) {
            case MATCH_EXPR ->            this.visitMatch(    sourceFile, scope, scopeVars, (MatchExpression)     statement);
            case REFERENCE_EXPR ->        this.visitReference(sourceFile, scope, scopeVars, (ReferenceExpression) statement);
            case BOOLEAN_EXPR ->          this.visitBoolean(  sourceFile, scope, scopeVars, (BooleanExpression)   statement);
            case NUMBER_EXPR ->           this.visitNumber(   sourceFile, scope, scopeVars, (NumberExpression)    statement);
            case STRING_EXPR ->           this.visitString(   sourceFile, scope, scopeVars, (StringExpression)    statement);
            case ARRAY_EXPR ->            this.visitArray(    sourceFile, scope, scopeVars, (ArrayInitExpression) statement);
            case BINARY_EXPR ->           this.visitBinary(   sourceFile, scope, scopeVars, (BinaryExpression)    statement);
            case UNARY_EXPR ->            this.visitUnary(    sourceFile, scope, scopeVars, (UnaryExpression)     statement);
            case LITERAL_EXPR ->          this.visitLiteral(  sourceFile, scope, scopeVars, (LiteralExpression)   statement);
            case CALL_EXPR ->             this.visitCall(     sourceFile, scope, scopeVars, (CallExpression)      statement);

            default -> ValidationException.UNMATCHED_STATEMENT
                    .throwException(sourceFile, statement.position(),
                            statement.type(), "Expressions");
        }
    }
    //@formatter:on

    public abstract void visitClassType(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, ClassTypeStatement statement);

    public abstract void visitFunction(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, FunctionStatement statement);

    public abstract void visitStructureType(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, StructTypeStatement statement);

    public abstract void visitConstruct(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, ConstructStatement statement);

    public abstract void visitVariable(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, VariableStatement statement);

    public abstract void visitLinkedNotice(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, LinkedNoticeStatement statement);

    public abstract void visitUDTDeclaration(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, UDTDeclareStatement udtDeclareStatement);

    public abstract void visitBreak(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, BreakStatement statement);

    public abstract void visitContinue(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, ContinueStatement statement);

    public abstract void visitFor(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, ForStatement statement);

    public abstract void visitDoWhile(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, DoWhileStatement statement);

    public abstract void visitIf(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, IfStatement statement);

    public abstract void visitLoop(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, LoopStatement statement);

    public abstract void visitReturn(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, ReturnStatement statement);

    public abstract void visitSwitch(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, SwitchStatement statement);

    public abstract void visitUnreachable(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, UnreachableStatement statement);

    public abstract void visitWhile(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, WhileStatement statement);

    public abstract void visitYield(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, YieldStatement statement);

    public abstract void visitBody(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, BodyStatement statement);

    public abstract void visitNative(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, NativeStatement statement);

    public abstract void visitMatch(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, MatchExpression statement);

    public abstract void visitReference(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, ReferenceExpression statement);

    public abstract void visitBoolean(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, BooleanExpression statement);

    public abstract void visitNumber(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, NumberExpression statement);

    public abstract void visitString(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, StringExpression statement);

    public abstract void visitArray(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, ArrayInitExpression statement);

    public abstract void visitBinary(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, BinaryExpression statement);

    public abstract void visitUnary(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, UnaryExpression statement);

    public abstract void visitLiteral(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, LiteralExpression statement);

    public abstract void visitCall(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, CallExpression statement);

}

