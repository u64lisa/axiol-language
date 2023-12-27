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
import axiol.types.Reference;
import axiol.types.ScopeVariable;
import axiol.types.SimpleType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class StaticAnalysis implements RootNodeProcessor<Void> {

    private final Mangler mangler = new Mangler();
    private final AnalyseContext analyseContext = new AnalyseContext();

    private final NumberRangeCheck I8_Check  = new NumberRangeCheck(0x7F,                -0x80);
    private final NumberRangeCheck I16_Check = new NumberRangeCheck(0x7FFF,              -0x8000);
    private final NumberRangeCheck I32_Check = new NumberRangeCheck(0x7FFFFFFF,          -0x80000000);
    private final NumberRangeCheck I64_Check = new NumberRangeCheck(0x7fffffffffffffffL, -0x8000000000000000L);

    private final NumberRangeCheck U8_Check  = new NumberRangeCheck(0xFF,                0x00);
    private final NumberRangeCheck U16_Check = new NumberRangeCheck(0xFFFF,              0x0000);
    private final NumberRangeCheck U32_Check = new NumberRangeCheck(0xFFFFFFFFL,         0x00000000);
    private final NumberRangeCheck U64_Check = new NumberRangeCheck(0xFFFFFFFFFFFFFFFFL, 0x0000000000000000);

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

        // empty root scope bcs pathing
        this.analyseDefinitions("", rootNode);

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

    private void analyseDefinitions(String scope, Statement statement) {
        String splitElement = scope.isEmpty() ? "" : "/";
        switch (statement.type()) {
            case ROOT -> {
                RootNode rootNode = (RootNode) statement;
                for (Statement rootNodeStatement : rootNode.getStatements()) {
                    this.analyseDefinitions("", rootNodeStatement);
                }
            }
            case CLASS_TYPE_STATEMENT -> {
                ClassTypeStatement classTypeStatement = (ClassTypeStatement) statement;
                scope = scope + splitElement + classTypeStatement.getName();

                this.analyseDefinitions(scope, classTypeStatement.getBodyStatement());
                this.analyseContext.getClasses().put(scope, classTypeStatement.getReference());
            }
            case STRUCT_TYPE_STATEMENT -> {
                StructTypeStatement structTypeStatement = (StructTypeStatement) statement;
                scope = scope + splitElement + structTypeStatement.getName();

                this.analyseContext.getStructures().put(scope, structTypeStatement.getReference());

                for (Parameter parameter : structTypeStatement.getEntries()) {
                    String scopeCopy = scope + splitElement + parameter.getName();
                    this.analyseContext.getStructuresFields().put(scopeCopy, structTypeStatement.getReference());
                }
            }

            case FUNCTION_STATEMENT -> {
                FunctionStatement functionStatement = (FunctionStatement) statement;
                scope = scope + splitElement + functionStatement.getName();
                this.analyseDefinitions(scope, functionStatement.getBodyStatement());
                this.analyseContext.getFunctions().put(scope, functionStatement.getReference());
            }
            case BODY_STATEMENT -> {
                BodyStatement bodyStatement = (BodyStatement) statement;
                for (Statement current : bodyStatement.getStatements()) {
                    this.analyseDefinitions(scope, current);
                }
            }
            case VAR_STATEMENT -> {
                VariableStatement variableStatement = (VariableStatement) statement;
                scope = scope + splitElement + variableStatement.getName();
                this.analyseContext.getVariable().put(scope, variableStatement.getReference());
            }
            case UDT_DECLARE_STATEMENT -> {
                UDTDeclareStatement udtDeclareStatement = (UDTDeclareStatement) statement;
                scope = scope + splitElement + udtDeclareStatement.getReferenceName();
                this.analyseContext.getUdt().put(scope, udtDeclareStatement.getReference());
            }
        }
    }

    private void analyseClassType(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, ClassTypeStatement statement) {
        String mangel = this.mangler.mangelClass(scope, statement);

        String splitElement = scope.isEmpty() ? "" : "/";
        scope = scope + splitElement + statement.getName();

        if (this.analyseContext.checkMangel(mangel)) {
            statement.getBodyStatement().getStatements().stream()
                    .filter(current -> current.type() == NodeType.VAR_STATEMENT)
                    .map(current -> (VariableStatement) current)
                    .forEachOrdered(varStatement -> this.analyseVariable(sourceFile, mangel, scopeMangled, scopeVars, varStatement));

            for (Statement statements : statement.getBodyStatement().getStatements()) {
                if (statements.type() == NodeType.VAR_STATEMENT)
                    continue;

                this.processStatement(sourceFile, scope, mangel, scopeVars, statements);
            }
            return;
        }
        ValidationException.DUPLICATE.throwException(sourceFile, statement.position(), "class", statement.getName());
    }

    private void analyseFunction(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, FunctionStatement statement) {
        String mangel = this.mangler.mangelFunction(scope, statement);

        String splitElement = scope.isEmpty() ? "" : "/";
        scope = scope + splitElement + statement.getName();

        if (this.analyseContext.checkMangel(mangel)) {
            for (Parameter parameter : statement.getParameters()) {
                scopeVars.add(new ScopeVariable(parameter.getName(), parameter.getParsedType()));
            }

            for (Statement statements : statement.getBodyStatement().getStatements()) {
                this.processStatement(sourceFile, scope, mangel, scopeVars, statements);
            }
            return;
        }
        ValidationException.DUPLICATE.throwException(sourceFile, statement.position(), "function", statement.getName());
    }

    private void analyseStructureType(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, StructTypeStatement statement) {
        String mangel = this.mangler.mangelStruct(scope, statement);
        if (this.analyseContext.checkMangel(mangel)) {

            return;
        }
        ValidationException.DUPLICATE.throwException(sourceFile, statement.position(), "struct", statement.getName());
    }

    private void analyseConstruct(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, ConstructStatement statement) {
        String mangel = this.mangler.mangelConstruct(scope, statement);
        if (this.analyseContext.checkMangel(mangel)) {

            return;
        }
        ValidationException.DUPLICATE.throwException(sourceFile, statement.position(), "construct", scope);
    }

    private void analyseVariable(SourceFile sourceFile, String scope, String scopeMangled, List<ScopeVariable> scopeVars, VariableStatement statement) {
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
        String[] rawPath = statement.getPath().split("/");
        String[] pathParts = null;

        if (rawPath.length > 1) {
            pathParts = new String[rawPath.length - 1];
            System.arraycopy(rawPath, 0, pathParts, 0, pathParts.length);
        } else {
            pathParts = new String[0];
        }
        StringBuilder path = new StringBuilder();
        for (String pathPart : pathParts) {
            path.append(".").append(pathPart);
        }

        boolean foundMatchingUdt = false;
        for (Map.Entry<String, Reference> stringReferenceEntry : this.analyseContext.getUdt().entrySet()) {
            String[] parts = stringReferenceEntry.getKey().split("/");
            String functionScopeName = parts[0];

            if (functionScopeName.equals(scope)) {
                foundMatchingUdt = true;
                break;
            }

        }

        if (!this.analyseContext.getFunctions().containsKey(statement.getPath()) && !foundMatchingUdt) {
            ValidationException.UNDECLARED_FUNCTION.throwException(sourceFile,
                    statement.position(), statement.getPath());
        }

        if (foundMatchingUdt) {
            for (Map.Entry<String, Reference> stringReferenceEntry : this.analyseContext.getUdt().entrySet()) {
                String[] parts = stringReferenceEntry.getKey().split("/");
                String functionScopeName = parts[0];

                if (functionScopeName.equals(scope)) {
                    Reference reference = stringReferenceEntry.getValue();

                    statement.setReference(reference);
                    break;
                }

            }
        } else {
            statement.setReference(this.analyseContext.getFunctions().get(statement.getPath()));
        }
    }

}
