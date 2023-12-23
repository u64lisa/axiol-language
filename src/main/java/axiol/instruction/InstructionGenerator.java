package axiol.instruction;

import axiol.instruction.reference.InstructionReference;
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
import axiol.parser.tree.statements.VariableStatement;
import axiol.parser.tree.statements.control.*;
import axiol.parser.tree.statements.oop.*;
import axiol.parser.tree.statements.special.NativeStatement;
import axiol.types.Reference;
import axiol.types.ReferenceStorage;

public class InstructionGenerator {
    private final InstructionSetBuilder instructionSet;
    private ReferenceStorage referenceStorage;

    private int referenceId = 0;

    private InstructionReference brakeLabel;
    private InstructionReference unreachableLabel;
    private InstructionReference continueLabel;

    private InstructionReference none;

    public InstructionGenerator() {
        instructionSet = new InstructionSetBuilder();

        none = instructionSet.createNoneReference(-99);
    }


    //@formatter:off
    public InstructionSet emit(RootNode rootNode) {
        this.referenceStorage = rootNode.getReferences();

        rootNode.getStatements().removeIf(statement -> statement.type() == NodeType.LINKED_STATEMENT);

        for (Statement statement : rootNode.getStatements()) {
            switch (statement.type()) {
                case CLASS_TYPE_STATEMENT ->  emitClassType((ClassTypeStatement) statement);
                case STRUCT_TYPE_STATEMENT -> emitStructureType((StructTypeStatement) statement);
                case FUNCTION_STATEMENT ->    emitFunctionType((FunctionStatement) statement);
                case VAR_STATEMENT ->         emitVarStatement((VariableStatement) statement);
                // todo attribute
                // todo enum
                default -> throw new IllegalArgumentException("unexpected statement '%s' at root of ast"
                        .formatted(statement.type().name()));
            }
        }

        return instructionSet.build();
    }

    public void loopBodyStatement(BodyStatement bodyStatement) {
        for (Statement statement : bodyStatement.getStatements()) {
            if (statement.type().equals(NodeType.BODY_STATEMENT)) {
                this.loopBodyStatement((BodyStatement) statement);
            } else {
                this.generateStatement(statement);
            }
        }
    }

    public InstructionReference generateStatement(Statement statement) {
        return switch (statement.type()) {
            // inner body statements
            case NATIVE_STATEMENT ->        emitNativeStatement(             (NativeStatement)            statement);
            case YIELD_STATEMENT ->         emitYieldStatement(              (YieldStatement)             statement);
            case WHILE_STATEMENT ->         emitWhileStatement(              (WhileStatement)             statement);
            case LOOP_STATEMENT ->          emitLoopStatement(               (LoopStatement)              statement);
            case UNREACHABLE_STATEMENT ->   emitUnreachableStatement(        (UnreachableStatement)       statement); //
            case RETURN_STATEMENT ->        emitReturnStatement(             (ReturnStatement)            statement);
            case SWITCH_STATEMENT ->        emitSwitchStatement(             (SwitchStatement)            statement);
            case IF_STATEMENT ->            emitIfStatement(                 (IfStatement)                statement);
            case FOR_STATEMENT ->           emitForStatement(                (ForStatement)               statement);
            case DO_WHILE_STATEMENT ->      emitDoWhileStatement(            (DoWhileStatement)           statement);
            case CONTINUE_STATEMENT ->      emitContinueStatement(           (ContinueStatement)          statement); //
            case CONSTRUCT_STATEMENT ->     emitConstructStatement(          (ConstructStatement)         statement);
            case BREAK_STATEMENT ->         emitBreakStatement(              (BreakStatement)             statement); //
            case UDT_DECLARE_STATEMENT ->   emitUDTDeclareStatement(         (UDTDeclareStatement)        statement);
            case VAR_STATEMENT ->           emitVarStatement(                (VariableStatement)          statement);
            case STRUCT_TYPE_STATEMENT ->   emitStructureType(               (StructTypeStatement)        statement);
            case FUNCTION_STATEMENT ->      emitFunctionType(                (FunctionStatement)          statement);
            case CLASS_TYPE_STATEMENT ->    emitClassType(                   (ClassTypeStatement)         statement);
            // expressions
            case ARRAY_EXPR ->              emitArrayExpression(             (ArrayInitExpression)        statement);
            case CALL_EXPR ->               emitCallExpression(              (CallExpression)             statement);
            case LITERAL_EXPR ->            emitLiteralExpression(           (LiteralExpression)          statement);
            case UNARY_EXPR ->              emitUnaryExpression(             (UnaryExpression)            statement);
            case BINARY_EXPR ->             emitBinaryExpression(            (BinaryExpression)           statement);
            case STRING_EXPR ->             emitStringExpression(            (StringExpression)           statement); //
            case NUMBER_EXPR ->             emitNumberExpression(            (NumberExpression)           statement); //
            case BOOLEAN_EXPR ->            emitBooleanExpression(           (BooleanExpression)          statement); //
            case ELEMENT_REFERENCE_EXPR ->  emitElementReferenceExpression(  (ElementReferenceExpression) statement);
            case MATCH_EXPR ->              emitMatchExpression(             (MatchExpression)            statement);

            // linking, root
            default -> throw new IllegalArgumentException("unexpected statement '%s' at body!"
                    .formatted(statement.type().name()));
        };
    }
    //@formatter:on

    private InstructionReference emitMatchExpression(MatchExpression statement) {
        return null;
    }

    private InstructionReference emitElementReferenceExpression(ElementReferenceExpression statement) {
        return null;
    }

    private InstructionReference emitBooleanExpression(BooleanExpression statement) {
        InstructionReference booleanReference = instructionSet.createBooleanReference(referenceId);
        instructionSet.instruction(OpCode.MOVE, builder -> builder
                .referenceOperand(booleanReference)
                .booleanOperand(statement.isValue()));

        return booleanReference;
    }

    private InstructionReference emitNumberExpression(NumberExpression statement) {
        InstructionReference reference = instructionSet.createNumberReference(statement.getType(), referenceId);
        instructionSet.instruction(OpCode.MOVE, builder -> builder
                .referenceOperand(reference)
                .numberOperand(statement.getType().getPrimitiveTypes(), statement.getNumberValue()));

        return reference;
    }

    private InstructionReference emitStringExpression(StringExpression statement) {
        instructionSet.instruction(OpCode.MOVE, builder -> builder
                .referenceOperand(this.instructionSet.createStringReference(referenceId))
                .stringOperand(statement.getValue()));
        return none;
    }

    private InstructionReference emitBinaryExpression(BinaryExpression statement) {
        return null;
    }

    private InstructionReference emitUnaryExpression(UnaryExpression statement) {
        InstructionReference proprietor = this.instructionSet.createDataReference(".unary", statement.valuedType(), referenceId);
        InstructionReference value = this.generateStatement(statement.getValue());

        OpCode opCode = switch (statement.getOperator()) {
            case NOT ->   OpCode.NEGATE;
            case NOR ->   OpCode.NEGATE_OR;
            case MINUS -> OpCode.SUBSTR;
            default -> throw new IllegalArgumentException("Unknown unary operation");
        };
        this.instructionSet.instruction(opCode, builder -> builder
                .referenceOperand(proprietor)
                .referenceOperand(value));

        return proprietor;
    }

    private InstructionReference emitLiteralExpression(LiteralExpression statement) {
        return null;

    }

    private InstructionReference emitCallExpression(CallExpression statement) {
        return null;
    }

    private InstructionReference emitArrayExpression(ArrayInitExpression statement) {
        return null;
    }

    private InstructionReference emitSwitchStatement(SwitchStatement statement) {
        return null;
    }

    private InstructionReference emitVarStatement(VariableStatement statement) {
        return null;
    }

    private InstructionReference emitUDTDeclareStatement(UDTDeclareStatement statement) {
        return null;
    }

    private InstructionReference emitBreakStatement(BreakStatement statement) {
        this.instructionSet.instruction(OpCode.GOTO,
                builder -> builder.referenceOperand(this.brakeLabel));
        return none;
    }

    private InstructionReference emitConstructStatement(ConstructStatement statement) {
        return null;
    }

    private InstructionReference emitContinueStatement(ContinueStatement statement) {
        this.instructionSet.instruction(OpCode.GOTO,
                builder -> builder.referenceOperand(this.continueLabel));
        return none;
    }

    private InstructionReference emitIfStatement(IfStatement statement) {
        return null;
    }

    private InstructionReference emitForStatement(ForStatement statement) {
        return null;
    }

    private InstructionReference emitDoWhileStatement(DoWhileStatement statement) {
        return null;
    }

    private InstructionReference emitReturnStatement(ReturnStatement statement) {
        return null;
    }

    private InstructionReference emitUnreachableStatement(UnreachableStatement statement) {
        this.instructionSet.instruction(OpCode.GOTO,
                builder -> builder.referenceOperand(unreachableLabel));
        return none;
    }

    private InstructionReference emitNativeStatement(NativeStatement statement) {
        return null;
    }

    private InstructionReference emitWhileStatement(WhileStatement statement) {
        return null;
    }

    private InstructionReference emitLoopStatement(LoopStatement statement) {
        return null;
    }

    private InstructionReference emitYieldStatement(YieldStatement statement) {
        return null;
    }

    private InstructionReference emitFunctionType(FunctionStatement statement) {

        this.loopBodyStatement(statement.getBodyStatement());
        return null;
    }

    private InstructionReference emitStructureType(StructTypeStatement statement) {

        return null;
    }

    private InstructionReference emitClassType(ClassTypeStatement statement) {

        this.loopBodyStatement(statement.getBodyStatement());
        return null;
    }

}
