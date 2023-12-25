package axiol.instruction;

import axiol.instruction.reference.InstructionReference;
import axiol.instruction.value.NumberInstructionOperand;
import axiol.parser.expression.Operator;
import axiol.parser.statement.Parameter;
import axiol.parser.tree.Expression;
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
import axiol.types.PrimitiveTypes;
import axiol.types.Reference;
import axiol.types.ReferenceStorage;
import axiol.types.SimpleType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        InstructionReference proprietor = this.instructionSet.createDataReference(".deref", statement.valuedType(), referenceId);
        InstructionReference pointer = this.generateStatement(statement);

        instructionSet.instruction(OpCode.LOAD, builder -> builder
                .referenceOperand(pointer)
                .referenceOperand(proprietor));

        return proprietor;
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

    public InstructionReference emitArrayIndexWrite(BinaryExpression leftExpression) {
        InstructionReference leftArrayReference = generateStatement(leftExpression.getLeftAssociate());
        InstructionReference rightArrayIndex = generateStatement(leftExpression.getRightAssociate());
        InstructionReference right = generateStatement(leftExpression.getRightAssociate());

        assert right.getValueType().assetEqualityFor(leftArrayReference.getValueType().increaseArrayDepth(1));

        instructionSet.instruction(OpCode.STORE, builder -> builder
                .referenceOperand(leftArrayReference)
                .referenceOperand(rightArrayIndex)
                .referenceOperand(right));

        return right;
    }

    public InstructionReference emitArrayIndexRead(BinaryExpression binaryExpression) {
        InstructionReference proprietor = instructionSet.createDataReference(".arr",
                binaryExpression.valuedType().increaseArrayDepth(1), referenceId);
        InstructionReference left = generateStatement(binaryExpression.getLeftAssociate());
        InstructionReference right = generateStatement(binaryExpression.getRightAssociate());

        instructionSet.instruction(OpCode.LOAD, builder -> builder
                .referenceOperand(proprietor)
                .referenceOperand(left)
                .referenceOperand(right));

        return proprietor;
    }

    public InstructionReference emitAssign(BinaryExpression binaryExpression) {
        InstructionReference proprietor = null;
        Expression left = binaryExpression.getLeftAssociate();
        Expression right = binaryExpression.getRightAssociate();

        InstructionReference leftReference = generateStatement(left);
        InstructionReference rightReference = generateStatement(right);
        proprietor = leftReference;

        assert left.valuedType().assetEqualityFor(right.valuedType());

        instructionSet.instruction(OpCode.MOVE, builder -> builder
                .referenceOperand(leftReference)
                .referenceOperand(rightReference));

        return proprietor;
    }

    // reference = (stateExpression) ? (ternaryLeft : ternaryRight)
    private InstructionReference emitTernary(Expression stateExpression, BinaryExpression ternaryValues) {
        InstructionReference proprietor = this.instructionSet.createDataReference(".ter", ternaryValues.valuedType(), referenceId);

        InstructionReference condition = generateStatement(stateExpression);

        InstructionReference trueValue = generateStatement(ternaryValues.getRightAssociate());
        InstructionReference falseValue = generateStatement(ternaryValues.getRightAssociate());

        InstructionReference endLabel = instructionSet.createLabel(".ter_end", referenceId);

        instructionSet.instruction(OpCode.GOTO_IF, builder -> builder
                .referenceOperand(condition)
                .referenceOperand(endLabel)
                .referenceOperand(trueValue));

        instructionSet.instruction(OpCode.MOVE, builder -> builder
                .referenceOperand(proprietor)
                .referenceOperand(falseValue));

        instructionSet.instruction(OpCode.LABEL, builder -> builder
                .referenceOperand(endLabel));

        return proprietor;
    }

    private final Operator[] assignOperators = {
            Operator.ASSIGN,
            Operator.MIN_ASSIGN,
            Operator.MUL_ASSIGN,
            Operator.DIVIDE_ASSIGN,
            Operator.XOR_ASSIGN,
            Operator.NOR_ASSIGN,
            Operator.QUESTION_ASSIGN,
            Operator.OR_ASSIGN,
    };

    private InstructionReference emitBinaryExpression(BinaryExpression statement) {
        InstructionReference proprietor = this.instructionSet.createDataReference(".bin", statement.valuedType(), referenceId);
        SimpleType type = statement.valuedType();
        Operator operator = statement.getOperator();

        for (Operator assignOperator : assignOperators) {
            if (assignOperator == operator)
                return emitAssign(statement);
        }

        // array read
        // value = array[1]
        if (operator == Operator.ARRAY) {
            return emitArrayIndexRead(statement);
        }
        // array write
        // array[1] = value;
        if (statement.getLeftAssociate() instanceof BinaryExpression leftExpression &&
                leftExpression.getOperator() == Operator.ARRAY) {
            return emitArrayIndexWrite(statement);
        }

        // binLeft(bool) ? binRight(ternary(binLeft : binRight))
        if (statement.getRightAssociate() instanceof BinaryExpression rightExpression &&
                rightExpression.getOperator() == Operator.TERNARY) {
            return emitTernary(statement.getLeftAssociate(), rightExpression);
        }


        OpCode opCode = chooseBinaryOpCode(operator,
                !type.getType().getPrimitiveTypes().isSigned(),
                type.getType().getPrimitiveTypes().isFloating(),
                type.getType().getPrimitiveTypes().isBig());

        InstructionReference left = this.generateStatement(statement.getLeftAssociate());
        InstructionReference right = this.generateStatement(statement.getRightAssociate());

        SimpleType leftType = statement.getLeftAssociate().valuedType();
        SimpleType rightType = statement.getRightAssociate().valuedType();

        assert leftType.assetEqualityFor(rightType);

        instructionSet.instruction(OpCode.MOVE, builder -> builder
                .referenceOperand(proprietor)
                .referenceOperand(left));

        instructionSet.instruction(opCode, builder -> builder
                .referenceOperand(proprietor)
                .referenceOperand(right));

        return proprietor;
    }

    private OpCode chooseBinaryOpCode(Operator operator, boolean unsigned, boolean floating, boolean bigNumber) {
        if (unsigned && floating)
            throw new IllegalArgumentException("floating number can't be unsigned!");

        // unsigned - floating - signed
        return switch (operator) {
            case PLUS -> unsigned ? OpCode.ADD : floating ? OpCode.FLOATING_ADD : OpCode.ADD;
            case MINUS -> unsigned ? OpCode.SUB : floating ? OpCode.FLOATING_SUB : OpCode.SUB;

            case MULTIPLE -> unsigned ? OpCode.MULTIPLY : floating ? OpCode.FLOATING_MULTIPLY : OpCode.MULTIPLY;
            case DIVIDE -> unsigned ? OpCode.DIVIDE : floating ? OpCode.FLOATING_DIVIDE : OpCode.DIVIDE;
            case MOD -> unsigned ? OpCode.MODULO : floating ? OpCode.FLOATING_MODULO : OpCode.MODULO;

            case AND -> OpCode.AND;
            case OR -> OpCode.OR;
            case XOR -> OpCode.XOR;
            case NOT_EQUAL -> OpCode.NEGATED_EQUALS;
            case BIT_OR -> OpCode.BIT_OR;

            case SHIFT_LEFT -> OpCode.SHIFT_LEFT;
            case SHIFT_RIGHT -> OpCode.SHIFT_RIGHT;

            case MORE_THAN ->
                    unsigned ? OpCode.GREATER_THAN : floating ? OpCode.FLOATING_GREATER_THAN : OpCode.GREATER_THAN;
            case MORE_EQUAL ->
                    unsigned ? OpCode.GREATER_THAN_EQUAL : floating ? OpCode.FLOATING_GREATER_THAN_EQUAL : OpCode.GREATER_THAN_EQUAL;
            case LESS_THAN -> unsigned ? OpCode.LESS_THAN : floating ? OpCode.FLOATING_LESS_THAN : OpCode.LESS_THAN;
            case LESS_EQUAL ->
                    unsigned ? OpCode.LESS_THAN_EQUAL : floating ? OpCode.FLOATING_LESS_THAN_EQUAL : OpCode.LESS_THAN_EQUAL;

            case EQUAL_EQUAL -> floating ? OpCode.FLOATING_EQUALS : OpCode.EQUALS;

            default -> null;
        };
    }

    private InstructionReference emitUnaryExpression(UnaryExpression statement) {
        InstructionReference proprietor = this.instructionSet.createDataReference(".unary", statement.valuedType(), referenceId);
        InstructionReference value = this.generateStatement(statement.getValue());

        if (statement.getOperator() == Operator.INCREASE || statement.getOperator() == Operator.DECREASE) {
            InstructionReference oneReference = this.instructionSet.createNumberReference(PrimitiveTypes.I32.toType(), referenceId);
            this.instructionSet.instruction(OpCode.MOVE, builder -> builder
                    .referenceOperand(oneReference)
                    .numberOperand(PrimitiveTypes.I32, 1));

            boolean increase = statement.getOperator() == Operator.INCREASE;

            this.instructionSet.instruction(OpCode.MOVE, builder -> builder
                    .referenceOperand(proprietor)
                    .referenceOperand(value));
            this.instructionSet.instruction(increase ? OpCode.ADD : OpCode.SUB, builder -> builder
                    .referenceOperand(proprietor)
                    .referenceOperand(oneReference));

            return proprietor;
        }

        OpCode opCode = switch (statement.getOperator()) {
            case NOT -> OpCode.NEGATE;
            case NOR -> OpCode.NEGATE_OR;
            case MINUS -> OpCode.SUBSTR;

            default -> throw new IllegalArgumentException("Unknown unary operation");
        };
        this.instructionSet.instruction(opCode, builder -> builder
                .referenceOperand(proprietor)
                .referenceOperand(value));

        return proprietor;
    }

    // wrapping references
    private InstructionReference emitLiteralExpression(LiteralExpression statement) {
        InstructionReference proprietor = instructionSet.createDataReference(".lit", statement.valuedType(), referenceId);
        instructionSet.instruction(OpCode.MOVE, builder -> builder
                .referenceOperand(proprietor)
                .referenceOperand(new InstructionReference(statement.getReference(), referenceId)));

        return proprietor;
    }

    private InstructionReference emitCallExpression(CallExpression statement) {
        InstructionReference proprietor = instructionSet.createDataReference(".call", statement.valuedType(),referenceId);

        Map<Expression, InstructionReference> parameters = new HashMap<>();
        for (Expression parameter : statement.getParameters()) {
            InstructionReference reference = this.generateStatement(parameter);
            parameters.put(parameter, reference);
        }

        instructionSet.instruction(OpCode.CALL, builder -> {
            builder.referenceOperand(proprietor)
                    .referenceOperand(new InstructionReference(statement.getReference(), referenceId));

            parameters.forEach((expression, instructionReference) -> builder.referenceOperand(instructionReference));
        });

        return proprietor;
    }

    private InstructionReference emitArrayExpression(ArrayInitExpression statement) {
        InstructionReference proprietor = instructionSet.createDataReference(".arc", statement.valuedType(), referenceId);
        instructionSet.instruction(OpCode.ALLOC, builder -> builder
                .referenceOperand(proprietor)
                .numberOperand(PrimitiveTypes.I32, statement.getValues().size()));

        for (int i = 0; i < statement.getValues().size(); i++) {
            Expression element = statement.getValues().get(i);

            InstructionReference valueReference = generateStatement(element);
            NumberInstructionOperand index = new NumberInstructionOperand(PrimitiveTypes.I32.toType(), i);

            instructionSet.instruction(OpCode.STORE, builder -> builder
                    .referenceOperand(proprietor)
                    .numberOperand(index)
                    .referenceOperand(valueReference));
        }
        return proprietor;
    }


    private InstructionReference emitVarStatement(VariableStatement statement) {
        Optional<Reference> reference = this.referenceStorage.getReferenceToStatement(statement);
        if (reference.isEmpty())
            throw new IllegalStateException("Function without reference fn: '%s'!".formatted(statement.getName()));

        InstructionReference proprietor = new InstructionReference(reference.get(), referenceId);
        InstructionReference value = generateStatement(statement.getValue());

        assert statement.getValue().valuedType().assetEqualityFor(proprietor.getValueType());

        instructionSet.instruction(OpCode.MOVE, builder -> builder
                .referenceOperand(proprietor)
                .referenceOperand(value));

        return proprietor;
    }


    private InstructionReference emitIfStatement(IfStatement statement) {
        InstructionReference elseLabel = this.instructionSet.createLabel(".else", referenceId);
        InstructionReference endLabel = this.instructionSet.createLabel(".if_end", referenceId);

        InstructionReference condition = this.generateStatement(statement.getCondition());

        instructionSet.instruction(OpCode.GOTO_IF, builder -> builder
                .referenceOperand(condition)
                .referenceOperand(elseLabel));

        this.loopBodyStatement(statement.getBody());
        instructionSet.instruction(OpCode.GOTO, builder -> builder
                .referenceOperand(endLabel));

        instructionSet.instruction(OpCode.LABEL, builder -> builder
                .referenceOperand(elseLabel));
        this.generateStatement(statement.getElseStatement());

        instructionSet.instruction(OpCode.LABEL, builder -> builder
                .referenceOperand(endLabel));

        return none;
    }

    private InstructionReference emitForStatement(ForStatement statement) {
        return null;
    }

    private InstructionReference emitSwitchStatement(SwitchStatement statement) {
        return null;
    }

    private InstructionReference emitDoWhileStatement(DoWhileStatement statement) {
        return null;
    }

    private InstructionReference emitContinueStatement(ContinueStatement statement) {
        this.instructionSet.instruction(OpCode.GOTO,
                builder -> builder.referenceOperand(this.continueLabel));
        return none;
    }

    private InstructionReference emitReturnStatement(ReturnStatement statement) {

        instructionSet.instruction(OpCode.RETURN, builder -> builder
                .referenceOperand(statement.getValue() == null ?
                        none :
                        generateStatement(statement.getValue())
                ));

        return none;
    }

    private InstructionReference emitUnreachableStatement(UnreachableStatement statement) {
        this.instructionSet.instruction(OpCode.GOTO,
                builder -> builder.referenceOperand(unreachableLabel));
        return none;
    }

    private InstructionReference emitBreakStatement(BreakStatement statement) {
        this.instructionSet.instruction(OpCode.GOTO,
                builder -> builder.referenceOperand(this.brakeLabel));
        return none;
    }

    private InstructionReference emitNativeStatement(NativeStatement statement) {
        for (NativeStatement.NativeInstruction instruction : statement.getInstructions()) {
            switch (statement.getType()) {
                case ASM -> {
                    instructionSet.instruction(OpCode.INLINE_ASSEMBLY, builder -> builder
                            .stringOperand(statement.getArchitecture().name())
                            .stringOperand(instruction.getLine()));
                }
                case IR -> {

                }
            }
        }
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

    private InstructionReference emitUDTDeclareStatement(UDTDeclareStatement statement) {

        return null;
    }

    private InstructionReference emitConstructStatement(ConstructStatement statement) {
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
