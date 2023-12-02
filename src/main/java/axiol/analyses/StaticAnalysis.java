package axiol.analyses;

import axiol.mangler.Mangler;
import axiol.parser.RootNodeProcessor;
import axiol.parser.statement.Parameter;
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
import axiol.types.SimpleType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaticAnalysis implements RootNodeProcessor {

    private final Mangler mangler = new Mangler();
    private final AnalyseContext analyseContext = new AnalyseContext();

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
            this.processStatement(sourceFile, "global", new ArrayList<>(), statement);
        }
        return rootNode;
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
            this.processExpressions(sourceFile, scopeVars, statement);
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
            case CLASS_TYPE_STATEMENT ->  this.analyseClassType(    sourceFile, scope, scopeVars, (ClassTypeStatement)    statement);
            case FUNCTION_STATEMENT ->    this.analyseFunction(     sourceFile, scope, scopeVars, (FunctionStatement)     statement);
            case STRUCT_TYPE_STATEMENT -> this.analyseStructureType(sourceFile, scope, scopeVars, (StructTypeStatement)   statement);
            case CONSTRUCT_STATEMENT ->   this.analyseConstruct(    sourceFile, scope, scopeVars, (ConstructStatement)    statement);
            case VAR_STATEMENT ->         this.analyseVariable(     sourceFile, scope, scopeVars, (VariableStatement)     statement);
            case LINKED_STATEMENT ->      this.analyseLinkedNotice( sourceFile, scope, scopeVars, (LinkedNoticeStatement) statement);

            default -> ValidationException.UNMATCHED_STATEMENT
                    .throwException(sourceFile, statement.position(),
                            statement.type(), "Definition");
        }
    }

    public void processDeclarationStatements(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, Statement statement) {
        if (statement instanceof UDTDeclareStatement udtDeclareStatement) {
            this.analyseUDTDeclaration(sourceFile, scope, scopeVars, udtDeclareStatement);
        }
    }

    public void processControlFlowStatements(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, Statement statement) {
        switch (statement.type()) {
            case BREAK_STATEMENT ->       this.analyseBreak(      sourceFile, scope, scopeVars, (BreakStatement)       statement);
            case FOR_STATEMENT ->         this.analyseFor(        sourceFile, scope, scopeVars, (ForStatement)         statement);
            case CONTINUE_STATEMENT ->    this.analyseContinue(   sourceFile, scope, scopeVars, (ContinueStatement)    statement);
            case DO_WHILE_STATEMENT ->    this.analyseDoWhile(    sourceFile, scope, scopeVars, (DoWhileStatement)     statement);
            case IF_STATEMENT ->          this.analyseIf(         sourceFile, scope, scopeVars, (IfStatement)          statement);
            case LOOP_STATEMENT ->        this.analyseLoop(       sourceFile, scope, scopeVars, (LoopStatement)        statement);
            case RETURN_STATEMENT ->      this.analyseReturn(     sourceFile, scope, scopeVars, (ReturnStatement)      statement);
            case SWITCH_STATEMENT ->      this.analyseSwitch(     sourceFile, scope, scopeVars, (SwitchStatement)      statement);
            case UNREACHABLE_STATEMENT -> this.analyseUnreachable(sourceFile, scope, scopeVars, (UnreachableStatement) statement);
            case WHILE_STATEMENT ->       this.analyseWhile(      sourceFile, scope, scopeVars, (WhileStatement)       statement);
            case YIELD_STATEMENT ->       this.analyseYield(      sourceFile, scope, scopeVars, (YieldStatement)       statement);

            default -> ValidationException.UNMATCHED_STATEMENT
                    .throwException(sourceFile, statement.position(),
                            statement.type(), "ControlFlow");
        }
    }

    public void processSpecialCasesStatements(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, Statement statement) {
        switch (statement.type()) {
            case BODY_STATEMENT ->        this.analyseBody(  sourceFile, scope, scopeVars, (BodyStatement)   statement);
            case NATIVE_STATEMENT ->      this.analyseNative(sourceFile, scope, scopeVars, (NativeStatement) statement);

            default -> ValidationException.UNMATCHED_STATEMENT
                    .throwException(sourceFile, statement.position(),
                            statement.type(), "SpecialCase");
        }
    }

    public void processExpressions(SourceFile sourceFile, List<ScopeVariable> scopeVars, Statement statement) {
        switch (statement.type()) {
            case MATCH_EXPR ->            this.analyseMatch(    sourceFile, scopeVars, (MatchExpression)     statement);
            case REFERENCE_EXPR ->        this.analyseReference(sourceFile, scopeVars, (ReferenceExpression) statement);
            case BOOLEAN_EXPR ->          this.analyseBoolean(  sourceFile, scopeVars, (BooleanExpression)   statement);
            case NUMBER_EXPR ->           this.analyseNumber(   sourceFile, scopeVars, (NumberExpression)    statement);
            case STRING_EXPR ->           this.analyseString(   sourceFile, scopeVars, (StringExpression)    statement);
            case ARRAY_EXPR ->            this.analyseArray(    sourceFile, scopeVars, (ArrayInitExpression) statement);
            case BINARY_EXPR ->           this.analyseBinary(   sourceFile, scopeVars, (BinaryExpression)    statement);
            case UNARY_EXPR ->            this.analyseUnary(    sourceFile, scopeVars, (UnaryExpression)     statement);
            case LITERAL_EXPR ->          this.analyseLiteral(  sourceFile, scopeVars, (LiteralExpression)   statement);
            case CALL_EXPR ->             this.analyseCall(     sourceFile, scopeVars, (CallExpression)      statement);

            default -> ValidationException.UNMATCHED_STATEMENT
                    .throwException(sourceFile, statement.position(),
                            statement.type(), "Expressions");
        }
    }
    //@formatter:on

    private void analyseClassType(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, ClassTypeStatement statement) {
        String mangel = this.mangler.mangelClass(scope, statement);
        if (this.analyseContext.checkMangel(mangel)) {
            statement.getBodyStatement().getStatements().stream()
                    .filter(current -> current.type() == NodeType.VAR_STATEMENT)
                    .map(current -> (VariableStatement) current)
                    .forEachOrdered(varStatement -> this.analyseVariable(sourceFile, mangel, scopeVars, varStatement));

            for (Statement statements : statement.getBodyStatement().getStatements()) {
                if (statements.type() == NodeType.VAR_STATEMENT)
                    continue;

                this.processStatement(sourceFile, mangel, scopeVars, statements);
            }
            return;
        }
        ValidationException.DUPLICATE.throwException(sourceFile, statement.position(), "class", statement.getName());
    }

    private void analyseFunction(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, FunctionStatement statement) {
        String mangel = this.mangler.mangelFunction(scope, statement);
        if (this.analyseContext.checkMangel(mangel)) {
            for (Parameter parameter : statement.getParameters()) {
                scopeVars.add(new ScopeVariable(parameter.getName(), parameter.getParsedType()));
            }

            for (Statement statements : statement.getBodyStatement().getStatements()) {
                this.processStatement(sourceFile, mangel, scopeVars, statements);
            }
            return;
        }
        ValidationException.DUPLICATE.throwException(sourceFile, statement.position(), "function", statement.getName());
    }

    private void analyseStructureType(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, StructTypeStatement statement) {
        String mangel = this.mangler.mangelStruct(scope, statement);
        if (this.analyseContext.checkMangel(mangel)) {

            return;
        }
        ValidationException.DUPLICATE.throwException(sourceFile, statement.position(), "struct", statement.getName());
    }

    private void analyseConstruct(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, ConstructStatement statement) {
        String mangel = this.mangler.mangelConstruct(scope, statement);
        if (this.analyseContext.checkMangel(mangel)) {

            return;
        }
        ValidationException.DUPLICATE.throwException(sourceFile, statement.position(), "construct", scope);
    }

    private void analyseVariable(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, VariableStatement statement) {
        String mangel = this.mangler.mangelVariable(scope, statement);
        if (this.analyseContext.checkMangel(mangel)) {
            SimpleType type = statement.getType();
            String name = statement.getName();

            if (scopeVars.stream().anyMatch(current -> current.getName().equals(name))) {

                ValidationException.DUPLICATED_VAR.throwException(sourceFile, statement.position(), name);
                return;
            }

            scopeVars.add(new ScopeVariable(name, type));
            return;
        }
        ValidationException.DUPLICATE.throwException(sourceFile, statement.position(), "variable", statement.getName());
    }

    private void analyseLinkedNotice(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, LinkedNoticeStatement statement) {
    }

    private void analyseUDTDeclaration(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, UDTDeclareStatement udtDeclareStatement) {
    }

    private void analyseBreak(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, BreakStatement statement) {
    }

    private void analyseContinue(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, ContinueStatement statement) {
    }

    private void analyseFor(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, ForStatement statement) {
    }

    private void analyseDoWhile(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, DoWhileStatement statement) {
    }

    private void analyseIf(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, IfStatement statement) {
    }

    private void analyseLoop(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, LoopStatement statement) {
    }

    private void analyseReturn(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, ReturnStatement statement) {
    }

    private void analyseSwitch(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, SwitchStatement statement) {
    }

    private void analyseUnreachable(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, UnreachableStatement statement) {
    }

    private void analyseWhile(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, WhileStatement statement) {
    }

    private void analyseYield(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, YieldStatement statement) {
    }

    private void analyseBody(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, BodyStatement statement) {
    }

    private void analyseNative(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, NativeStatement statement) {
    }

    private void analyseMatch(SourceFile sourceFile, List<ScopeVariable> scopeVars, MatchExpression statement) {
    }

    private void analyseReference(SourceFile sourceFile, List<ScopeVariable> scopeVars, ReferenceExpression statement) {
    }

    private void analyseBoolean(SourceFile sourceFile, List<ScopeVariable> scopeVars, BooleanExpression statement) {
    }

    private void analyseNumber(SourceFile sourceFile, List<ScopeVariable> scopeVars, NumberExpression statement) {
    }

    private void analyseString(SourceFile sourceFile, List<ScopeVariable> scopeVars, StringExpression statement) {
    }

    private void analyseArray(SourceFile sourceFile, List<ScopeVariable> scopeVars, ArrayInitExpression statement) {
    }

    private void analyseBinary(SourceFile sourceFile, List<ScopeVariable> scopeVars, BinaryExpression statement) {
    }

    private void analyseUnary(SourceFile sourceFile, List<ScopeVariable> scopeVars, UnaryExpression statement) {
    }

    private void analyseLiteral(SourceFile sourceFile, List<ScopeVariable> scopeVars, LiteralExpression statement) {
    }

    private void analyseCall(SourceFile sourceFile, List<ScopeVariable> scopeVars, CallExpression statement) {
    }

}
