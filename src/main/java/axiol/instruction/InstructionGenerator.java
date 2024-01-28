package axiol.instruction;

import axiol.instruction.reference.InstructionReference;
import axiol.instruction.value.NumberInstructionOperand;
import axiol.linker.LinkedSources;
import axiol.parser.expression.Operator;
import axiol.parser.statement.Parameter;
import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.RootNode;
import axiol.parser.tree.Statement;
import axiol.parser.tree.expressions.*;
import axiol.parser.tree.expressions.control.MatchExpression;
import axiol.parser.tree.expressions.extra.CastExpression;
import axiol.parser.tree.expressions.extra.ElementReferenceExpression;
import axiol.parser.tree.expressions.extra.StackAllocExpression;
import axiol.parser.tree.expressions.sub.BooleanExpression;
import axiol.parser.tree.expressions.sub.NumberExpression;
import axiol.parser.tree.expressions.sub.StringExpression;
import axiol.parser.tree.statements.BodyStatement;
import axiol.parser.tree.statements.VariableStatement;
import axiol.parser.tree.statements.control.*;
import axiol.parser.tree.statements.oop.*;
import axiol.parser.tree.statements.special.NativeStatement;
import axiol.parser.util.reference.Reference;
import axiol.types.Type;

import java.util.*;

public class InstructionGenerator {

    private static Operator[] ASSIGN_OPERATORS = {
            Operator.ASSIGN,
            Operator.MIN_ASSIGN,
            Operator.MUL_ASSIGN,
            Operator.DIVIDE_ASSIGN,
            Operator.XOR_ASSIGN,
            Operator.NOR_ASSIGN,
            Operator.QUESTION_ASSIGN,
            Operator.OR_ASSIGN,
    };

    private final InstructionSetBuilder instructionSet;

    private int referenceId = 0;

    private InstructionReference unreachableLabel;

    private InstructionReference loopBrakeLabel;
    private InstructionReference currentLoopBrakeLabel;
    private InstructionReference loopContinueLabel;
    private InstructionReference currentLoopContinueLabel;

    private InstructionReference brakeLabel;
    private InstructionReference currentBrakeLabel;
    private InstructionReference continueLabel;
    private InstructionReference currentContinueLabel;

    public InstructionGenerator() {
        instructionSet = new InstructionSetBuilder();
    }

    //@formatter:off
    public InstructionSet emit(LinkedSources linkedSources) {
        linkedSources.getStatements().removeIf(statement -> statement.type() == NodeType.LINKED_STATEMENT);

        LinkedList<Statement> statements = new LinkedList<>(linkedSources.getStatements());
        while (!statements.isEmpty()) {
            Statement statement = statements.poll();

            if (statement instanceof NamespaceStatement namespaceStatement) {
                statements.addAll(0, namespaceStatement.getBodyStatement().getStatements());
                continue;
            }
            // Each statement inside of this program gets its own procedure
            // some procedures are variable procedures and some a function
            // procedures.

            ProgramElement procedure = new ProgramElement(switch (statement.type()) {
                case FUNCTION_STATEMENT -> ProgramType.FUNCTION;
                case VAR_STATEMENT -> ProgramType.VARIABLE;
                case BODY_STATEMENT -> ProgramType.CODE;
                default -> throw new RuntimeException("Invalid statement inside procedure: %s".formatted(statement.type()));
            });

            referenceId = 0;
            generateStatement(statement, procedure);
            instructionSet.addProgramElement(procedure);
        }

        return instructionSet.build();
    }
    //public InstructionSet emit(LinkedSources linkedSources) {
    //
    //    linkedSources.getStatements().removeIf(statement -> statement.type() == NodeType.LINKED_STATEMENT);
    //
    //    for (Statement statement : linkedSources.getStatements()) {
    //        switch (statement.type()) {
    //            case CLASS_TYPE_STATEMENT ->  emitClassType((ClassTypeStatement) statement);
    //            case STRUCT_TYPE_STATEMENT -> emitStructureType((StructTypeStatement) statement);
    //            case FUNCTION_STATEMENT ->    emitFunctionType((FunctionStatement) statement);
    //            case VAR_STATEMENT ->         emitVarStatement((VariableStatement) statement);
    //            case NAMESPACE_STATEMENT ->   emitNamespaceStatement((NamespaceStatement) statement);
    //            // todo attribute
    //            case ENUM_TYPE_STATEMENT ->   emitEnumType((EnumTypeStatement) statement);
    //            default -> throw new IllegalArgumentException("unexpected statement '%s' at root of ast"
    //                    .formatted(statement.type().name()));
    //        }
    //    }
    //
    //    return instructionSet.build();
    //}

    public InstructionReference loopBodyStatement(BodyStatement bodyStatement, ProgramElement element) {
        for (Statement statement : bodyStatement.getStatements()) {
            if (statement.type().equals(NodeType.BODY_STATEMENT)) {
                this.loopBodyStatement((BodyStatement) statement, element);
            } else {
                this.generateStatement(statement, element);
            }
        }
        return null;
    }

    public InstructionReference generateStatement(Statement statement, ProgramElement element) {

        return switch (statement.type()) {
            // inner body statements
            case NATIVE_STATEMENT ->        emitNativeStatement(             (NativeStatement)            statement, element); //
            case YIELD_STATEMENT ->         emitYieldStatement(              (YieldStatement)             statement, element);
            case WHILE_STATEMENT ->         emitWhileStatement(              (WhileStatement)             statement, element); //
            case LOOP_STATEMENT ->          emitLoopStatement(               (LoopStatement)              statement, element); //
            case UNREACHABLE_STATEMENT ->   emitUnreachableStatement(        (UnreachableStatement)       statement, element); //
            case RETURN_STATEMENT ->        emitReturnStatement(             (ReturnStatement)            statement, element); //
            case SWITCH_STATEMENT ->        emitSwitchStatement(             (SwitchStatement)            statement, element); //
            case IF_STATEMENT ->            emitIfStatement(                 (IfStatement)                statement, element); //
            case FOR_STATEMENT ->           emitForStatement(                (ForStatement)               statement, element); //
            case DO_WHILE_STATEMENT ->      emitDoWhileStatement(            (DoWhileStatement)           statement, element); //
            case CONTINUE_STATEMENT ->      emitContinueStatement(           (ContinueStatement)          statement, element); //
            case CONSTRUCT_STATEMENT ->     emitConstructStatement(          (ConstructStatement)         statement, element);
            case BREAK_STATEMENT ->         emitBreakStatement(              (BreakStatement)             statement, element); //
            case UDT_DECLARE_STATEMENT ->   emitUDTDeclareStatement(         (UDTDeclareStatement)        statement, element);
            case VAR_STATEMENT ->           emitVarStatement(                (VariableStatement)          statement, element); //
            case STRUCT_TYPE_STATEMENT ->   emitStructureType(               (StructTypeStatement)        statement, element);
            case FUNCTION_STATEMENT ->      emitFunctionType(                (FunctionStatement)          statement, element);
            case CLASS_TYPE_STATEMENT ->    emitClassType(                   (ClassTypeStatement)         statement, element);
            // expressions
            case ARRAY_EXPR ->              emitArrayExpression(             (ArrayInitExpression)        statement, element); //
            case CALL_EXPR ->               emitCallExpression(              (CallExpression)             statement, element); //
            case LITERAL_EXPR ->            emitLiteralExpression(           (LiteralExpression)          statement, element); //
            case UNARY_EXPR ->              emitUnaryExpression(             (UnaryExpression)            statement, element); //
            case BINARY_EXPR ->             emitBinaryExpression(            (BinaryExpression)           statement, element); //
            case STRING_EXPR ->             emitStringExpression(            (StringExpression)           statement, element); //
            case NUMBER_EXPR ->             emitNumberExpression(            (NumberExpression)           statement, element); //
            case BOOLEAN_EXPR ->            emitBooleanExpression(           (BooleanExpression)          statement, element); //
            case ELEMENT_REFERENCE_EXPR ->  emitElementReferenceExpression(  (ElementReferenceExpression) statement, element); //
            case MATCH_EXPR ->              emitMatchExpression(             (MatchExpression)            statement, element); //
            case CAST_EXPR ->               emitCastExpression(              (CastExpression)             statement, element); //
            case STACK_ALLOC ->             emitStackAllocExpression(        (StackAllocExpression)       statement, element); //
                                                                                                                   
            case BODY_STATEMENT ->          loopBodyStatement(               (BodyStatement)              statement, element); //
            case EMPTY ->                   null;

            // linking, root
            default -> throw new IllegalArgumentException("unexpected statement '%s' at body!"
                    .formatted(statement.type().name()));
        };
    }
    //@formatter:on

    private InstructionReference emitElementReferenceExpression(ElementReferenceExpression statement, ProgramElement element) {
        InstructionReference proprietor = this.instructionSet.createDataReference(".deref", statement.valuedType(), referenceId);
        InstructionReference pointer = this.generateStatement(statement, element);

        element.instruction(OpCode.LOAD, builder -> builder
                .referenceOperand(pointer)
                .referenceOperand(proprietor));

        return proprietor;
    }

    private InstructionReference emitBooleanExpression(BooleanExpression statement, ProgramElement element) {
        InstructionReference reference = instructionSet.createBooleanReference(referenceId);
        element.instruction(OpCode.MOVE, builder -> builder
                .referenceOperand(reference)
                .booleanOperand(statement.isValue()));

        return reference;
    }

    private InstructionReference emitNumberExpression(NumberExpression statement, ProgramElement element) {
        InstructionReference reference = instructionSet.createNumberReference(statement.getType(), referenceId);
        element.instruction(OpCode.MOVE, builder -> builder
                .referenceOperand(reference)
                .numberOperand(statement.valuedType(), statement.getNumberValue()));

        return reference;
    }

    private InstructionReference emitStringExpression(StringExpression statement, ProgramElement element) {
        InstructionReference reference = instructionSet.createStringReference(referenceId);
        element.instruction(OpCode.MOVE, builder -> builder
                .referenceOperand(reference)
                .stringOperand(statement.getValue()));
        return reference;
    }

    private InstructionReference emitStackAllocExpression(StackAllocExpression statement, ProgramElement element) {
        InstructionReference proprietor = this.instructionSet.createDataReference(".alloc", statement.valuedType(), referenceId);

        InstructionReference size = this.instructionSet.createNumberReference(Type.I32, statement.getDepth().getNumberValue().intValue());

        element.instruction(OpCode.ALLOC, builder -> builder
                .referenceOperand(proprietor)
                .referenceOperand(size));

        return proprietor;
    }

    private InstructionReference emitCastExpression(CastExpression statement, ProgramElement element) {
        InstructionReference proprietor = this.instructionSet.createDataReference(".cast", statement.valuedType(), referenceId);
        InstructionReference reference = generateStatement(statement.getValue(), element);

        Type from = statement.valuedType();
        int sizeFrom = from.getBits() / 8;
        Type to = statement.valuedType();
        int sizeTo = from.getBits() / 8;

        element.instruction(
                sizeTo <= sizeFrom && !(from.isBig() || to.isBig()) ? ((from.isBig() || to.isBig()) ? OpCode.BIG_TRUNCATE : OpCode.TRUNCATE) :
                        from.isSigned() && to.isSigned() ? OpCode.SIGN_EXTEND :
                                from.isFloating() || to.isFloating() ? OpCode.FLOATING_EXTEND :
                                        from.isBig() && to.isBig() ? OpCode.BIG_ZERO_EXTEND : OpCode.ZERO_EXTEND, builder -> builder
                        .referenceOperand(proprietor)
                        .referenceOperand(reference));

        return proprietor;
    }

    public InstructionReference emitArrayIndexWrite(BinaryExpression leftExpression, ProgramElement element) {
        InstructionReference leftArrayReference = generateStatement(leftExpression.getLeftAssociate(), element);
        InstructionReference rightArrayIndex = generateStatement(leftExpression.getRightAssociate(), element);
        InstructionReference right = generateStatement(leftExpression.getRightAssociate(), element);

        assert right.getValueType().assetEqualityFor(leftArrayReference.getValueType().increaseArrayDepth(1));

        element.instruction(OpCode.STORE, builder -> builder
                .referenceOperand(leftArrayReference)
                .referenceOperand(rightArrayIndex)
                .referenceOperand(right));

        return right;
    }

    public InstructionReference emitArrayIndexRead(BinaryExpression binaryExpression, ProgramElement element) {
        InstructionReference proprietor = instructionSet.createDataReference(".arr",
                binaryExpression.valuedType().increaseArrayDepth(1), referenceId);
        InstructionReference left = generateStatement(binaryExpression.getLeftAssociate(), element);
        InstructionReference right = generateStatement(binaryExpression.getRightAssociate(), element);

        element.instruction(OpCode.LOAD, builder -> builder
                .referenceOperand(proprietor)
                .referenceOperand(left)
                .referenceOperand(right));

        return proprietor;
    }

    public InstructionReference emitAssign(BinaryExpression binaryExpression, ProgramElement element) {
        InstructionReference proprietor = null;
        Expression left = binaryExpression.getLeftAssociate();
        Expression right = binaryExpression.getRightAssociate();

        InstructionReference leftReference = generateStatement(left, element);
        InstructionReference rightReference = generateStatement(right, element);
        proprietor = leftReference;

        assert left.valuedType().assetEqualityFor(right.valuedType());

        element.instruction(OpCode.MOVE, builder -> builder
                .referenceOperand(leftReference)
                .referenceOperand(rightReference));

        return proprietor;
    }

    // reference = (stateExpression) ? (ternaryLeft : ternaryRight)
    private InstructionReference emitTernary(Expression stateExpression, BinaryExpression ternaryValues, ProgramElement element) {
        InstructionReference proprietor = this.instructionSet.createDataReference(".ter", ternaryValues.valuedType(), referenceId);

        InstructionReference condition = generateStatement(stateExpression, element);

        InstructionReference trueValue = generateStatement(ternaryValues.getRightAssociate(), element);
        InstructionReference falseValue = generateStatement(ternaryValues.getRightAssociate(), element);

        InstructionReference endLabel = instructionSet.createLabel(".ter_end", referenceId);

        element.instruction(OpCode.GOTO_IF, builder -> builder
                .referenceOperand(condition)
                .referenceOperand(endLabel)
                .referenceOperand(trueValue));

        element.instruction(OpCode.MOVE, builder -> builder
                .referenceOperand(proprietor)
                .referenceOperand(falseValue));

        element.instruction(OpCode.LABEL, builder -> builder
                .referenceOperand(endLabel));

        return proprietor;
    }

    public InstructionReference emitAndLogic(BinaryExpression binaryExpression, ProgramElement element) {
        InstructionReference endingReference = instructionSet.createLabel(".and_end", referenceId);
        InstructionReference proprietor = instructionSet.createDataReference(".and", binaryExpression.valuedType(), referenceId);

        element.instruction(OpCode.MOVE, builder -> builder
                .referenceOperand(proprietor)
                .numberOperand(binaryExpression.valuedType(), 1));

        InstructionReference leftReference = generateStatement(binaryExpression.getLeftAssociate(), element);
        InstructionReference rightReference = generateStatement(binaryExpression.getLeftAssociate(), element);

        element.instruction(OpCode.GOTO_IF, builder -> builder
                .referenceOperand(leftReference)
                .referenceOperand(endingReference));

        element.instruction(OpCode.GOTO_IF, builder -> builder
                .referenceOperand(rightReference)
                .referenceOperand(endingReference));

        element.instruction(OpCode.MOVE, builder -> builder
                .referenceOperand(proprietor)
                .numberOperand(binaryExpression.valuedType(), 1));

        element.instruction(OpCode.LABEL, builder -> builder
                .referenceOperand(endingReference));

        return proprietor;
    }

    public InstructionReference emitOrLogic(BinaryExpression binaryExpression, ProgramElement element) {
        InstructionReference endingReference = instructionSet.createLabel(".or_end", referenceId);
        InstructionReference proprietor = instructionSet.createDataReference(".or", binaryExpression.valuedType(), referenceId);
        InstructionReference orValue = instructionSet.createLabel(".or_val", referenceId);

        element.instruction(OpCode.MOVE, builder -> builder
                .referenceOperand(proprietor)
                .numberOperand(binaryExpression.valuedType(), 1));

        InstructionReference leftReference = generateStatement(binaryExpression.getLeftAssociate(), element);
        InstructionReference rightReference = generateStatement(binaryExpression.getLeftAssociate(), element);


        element.instruction(OpCode.GOTO_IF_NOT_EQ, builder -> builder
                .referenceOperand(leftReference)
                .referenceOperand(endingReference));

        element.instruction(OpCode.GOTO_IF_NOT_EQ, builder -> builder
                .referenceOperand(rightReference)
                .referenceOperand(endingReference));

        element.instruction(OpCode.GOTO, builder -> builder
                .referenceOperand(endingReference));

        element.instruction(OpCode.LABEL, builder -> builder
                .referenceOperand(orValue));

        element.instruction(OpCode.MOVE, builder -> builder
                .referenceOperand(proprietor)
                .numberOperand(binaryExpression.valuedType(), 1));

        element.instruction(OpCode.LABEL, builder -> builder
                .referenceOperand(endingReference));

        return proprietor;
    }

    private InstructionReference emitBinaryExpression(BinaryExpression statement, ProgramElement element) {
        InstructionReference proprietor = this.instructionSet.createDataReference(".bin", statement.valuedType(), referenceId);
        Type type = statement.valuedType();
        Operator operator = statement.getOperator();

        if (operator == Operator.OR)
            return emitOrLogic(statement, element);
        if (operator == Operator.AND)
            return emitAndLogic(statement, element);

        for (Operator assignOperator : ASSIGN_OPERATORS) {
            if (assignOperator == operator)
                return emitAssign(statement, element);
        }

        // array read
        // value = array[1]
        if (operator == Operator.ARRAY) {
            return emitArrayIndexRead(statement, element);
        }
        // array write
        // array[1] = value;
        if (statement.getLeftAssociate() instanceof BinaryExpression leftExpression &&
                leftExpression.getOperator() == Operator.ARRAY) {
            return emitArrayIndexWrite(statement, element);
        }

        // binLeft(bool) ? binRight(ternary(binLeft : binRight))
        if (statement.getRightAssociate() instanceof BinaryExpression rightExpression &&
                rightExpression.getOperator() == Operator.TERNARY) {
            return emitTernary(statement.getLeftAssociate(), rightExpression, element);
        }


        OpCode opCode = chooseBinaryOpCode(operator,
                !type.isSigned(),
                type.isFloating(),
                type.isBig());

        InstructionReference left = this.generateStatement(statement.getLeftAssociate(), element);
        InstructionReference right = this.generateStatement(statement.getRightAssociate(), element);

        Type leftType = statement.getLeftAssociate().valuedType();
        Type rightType = statement.getRightAssociate().valuedType();

        assert leftType.assetEqualityFor(rightType);

        element.instruction(OpCode.MOVE, builder -> builder
                .referenceOperand(proprietor)
                .referenceOperand(left));

        element.instruction(opCode, builder -> builder
                .referenceOperand(proprietor)
                .referenceOperand(right));

        return proprietor;
    }

    private OpCode chooseBinaryOpCode(Operator operator, boolean unsigned, boolean floating, boolean bigNumber) {
        if (unsigned && floating)
            throw new IllegalArgumentException("floating number can't be unsigned!");
        //@formatter:off
        return switch (operator) { // unsigned - floating - signed
            case PLUS -> unsigned || !floating ? OpCode.ADD : OpCode.FLOATING_ADD;
            case MINUS -> unsigned || !floating ? OpCode.SUB : OpCode.FLOATING_SUB;

            case MULTIPLE -> !floating ? (unsigned ? OpCode.UNSIGNED_MULTIPLY : OpCode.SIGNED_MULTIPLY) : OpCode.FLOATING_MULTIPLY;
            case DIVIDE ->   !floating ? (unsigned ? OpCode.UNSIGNED_DIVIDE   : OpCode.SIGNED_DIVIDE)   : OpCode.FLOATING_DIVIDE;
            case MOD ->      !floating ? (unsigned ? OpCode.UNSIGNED_MODULO   : OpCode.SIGNED_MODULO)   : OpCode.FLOATING_MODULO;

            case AND ->       OpCode.AND;
            case OR ->        OpCode.OR;                          // TODO ADD SIGNED OPS
            case XOR ->       OpCode.XOR;                         // TODO ALL CURRENT OPS ARE UNSIGNED
            case NOT_EQUAL -> OpCode.NEGATED_EQUALS;
            case BIT_OR ->    OpCode.BIT_OR;

            case SHIFT_LEFT ->  OpCode.SHIFT_LEFT;
            case SHIFT_RIGHT -> OpCode.SHIFT_RIGHT;

            case MORE_THAN ->   !floating ? (unsigned ? OpCode.UNSIGNED_GREATER_THAN       : OpCode.SIGNED_GREATER_THAN)       : OpCode.FLOATING_GREATER_THAN;
            case MORE_EQUAL ->  !floating ? (unsigned ? OpCode.UNSIGNED_GREATER_THAN_EQUAL : OpCode.SIGNED_GREATER_THAN_EQUAL) : OpCode.FLOATING_LESS_THAN;
            case LESS_THAN ->   !floating ? (unsigned ? OpCode.UNSIGNED_LESS_THAN          : OpCode.SIGNED_LESS_THAN)          : OpCode.FLOATING_GREATER_THAN_EQUAL;
            case LESS_EQUAL ->  !floating ? (unsigned ? OpCode.UNSIGNED_LESS_THAN_EQUAL    : OpCode.SIGNED_LESS_THAN_EQUAL)    : OpCode.FLOATING_LESS_THAN_EQUAL;
            case EQUAL_EQUAL -> floating ? OpCode.FLOATING_EQUALS : OpCode.EQUALS;

            default -> null;
        };
        //@formatter:on
    }

    private InstructionReference emitUnaryExpression(UnaryExpression statement, ProgramElement element) {
        InstructionReference proprietor = this.instructionSet.createDataReference(".unary", statement.valuedType(), referenceId);
        InstructionReference value = this.generateStatement(statement.getValue(), element);

        if (statement.getOperator() == Operator.INCREASE || statement.getOperator() == Operator.DECREASE) {
            InstructionReference oneReference = this.instructionSet.createNumberReference(Type.I32, referenceId);
            element.instruction(OpCode.MOVE, builder -> builder
                    .referenceOperand(oneReference)
                    .numberOperand(Type.I32, 1));

            boolean increase = statement.getOperator() == Operator.INCREASE;

            element.instruction(OpCode.MOVE, builder -> builder
                    .referenceOperand(proprietor)
                    .referenceOperand(value));
            element.instruction(increase ? OpCode.ADD : OpCode.SUB, builder -> builder
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
        element.instruction(opCode, builder -> builder
                .referenceOperand(proprietor)
                .referenceOperand(value));

        return proprietor;
    }

    // wrapping references
    private InstructionReference emitLiteralExpression(LiteralExpression statement, ProgramElement element) {
        InstructionReference proprietor = instructionSet.createDataReference(".lit", statement.valuedType(), referenceId);
        element.instruction(OpCode.MOVE, builder -> builder
                .referenceOperand(proprietor)
                .referenceOperand(new InstructionReference(statement.getReference(), referenceId)));

        return proprietor;
    }

    private InstructionReference emitCallExpression(CallExpression statement, ProgramElement element) {
        InstructionReference proprietor = instructionSet.createDataReference(".call", statement.valuedType(), referenceId);

        Map<Expression, InstructionReference> parameters = new HashMap<>();
        for (Expression parameter : statement.getParameters()) {
            InstructionReference reference = this.generateStatement(parameter, element);
            parameters.put(parameter, reference);
        }

        //instructionSet.instruction(OpCode.CALL, builder -> {
        //    builder.referenceOperand(proprietor) todo fix this reference filtering
        //            .referenceOperand(new InstructionReference(statement.getReference(), referenceId));
        //
        //    parameters.forEach((expression, instructionReference) -> builder.referenceOperand(instructionReference));
        //});

        return proprietor;
    }

    private InstructionReference emitArrayExpression(ArrayInitExpression statement, ProgramElement element) {
        InstructionReference proprietor = instructionSet.createDataReference(".arc", statement.valuedType(), referenceId);
        element.instruction(OpCode.ALLOC, builder -> builder
                .referenceOperand(proprietor)
                .numberOperand(Type.I32, statement.getValues().size()));

        for (int i = 0; i < statement.getValues().size(); i++) {
            Expression expression = statement.getValues().get(i);

            InstructionReference valueReference = generateStatement(expression, element);
            NumberInstructionOperand index = new NumberInstructionOperand(Type.I32, i);

            element.instruction(OpCode.STORE, builder -> builder
                    .referenceOperand(proprietor)
                    .numberOperand(index)
                    .referenceOperand(valueReference));
        }
        return proprietor;
    }


    private InstructionReference emitVarStatement(VariableStatement statement, ProgramElement element) {
        Reference reference = statement.getReference(); //todo this.referenceStorage.getReferenceToStatement(statement);

        InstructionReference proprietor = new InstructionReference(reference, referenceId);
        InstructionReference value = generateStatement(statement.getValue(), element);

        assert statement.getValue().valuedType().assetEqualityFor(proprietor.getValueType());

        element.instruction(OpCode.MOVE, builder -> builder
                .referenceOperand(proprietor)
                .referenceOperand(value));

        if (element.getType() == ProgramType.VARIABLE) {
            element.setReference(proprietor);
        }

        return proprietor;
    }


    private InstructionReference emitIfStatement(IfStatement statement, ProgramElement element) {
        InstructionReference elseLabel = this.instructionSet.createLabel(".else", referenceId);
        InstructionReference endLabel = this.instructionSet.createLabel(".if_end", referenceId);

        InstructionReference condition = this.generateStatement(statement.getCondition(), element);

        element.instruction(OpCode.GOTO_IF, builder -> builder
                .referenceOperand(condition)
                .referenceOperand(elseLabel));

        this.loopBodyStatement(statement.getBody(), element);
        element.instruction(OpCode.GOTO, builder -> builder
                .referenceOperand(endLabel));

        element.instruction(OpCode.LABEL, builder -> builder
                .referenceOperand(elseLabel));
        this.generateStatement(statement.getElseStatement(), element);

        element.instruction(OpCode.LABEL, builder -> builder
                .referenceOperand(endLabel));

        return null;
    }

    private InstructionReference emitContinueStatement(ContinueStatement statement, ProgramElement element) {
        element.instruction(OpCode.GOTO,
                builder -> builder.referenceOperand(this.continueLabel));
        return null;
    }

    private InstructionReference emitReturnStatement(ReturnStatement statement, ProgramElement element) {

        element.instruction(OpCode.RETURN, builder -> builder
                .referenceOperand(statement.getValue() == null ?
                        null :
                        generateStatement(statement.getValue(), element)
                ));

        return null;
    }

    private InstructionReference emitUnreachableStatement(UnreachableStatement statement, ProgramElement element) {
        element.instruction(OpCode.GOTO,
                builder -> builder.referenceOperand(unreachableLabel));
        return null;
    }

    private InstructionReference emitBreakStatement(BreakStatement statement, ProgramElement element) {
        element.instruction(OpCode.GOTO,
                builder -> builder.referenceOperand(this.brakeLabel));
        return null;
    }

    private InstructionReference emitNativeStatement(NativeStatement statement, ProgramElement element) {
        for (NativeStatement.NativeInstruction instruction : statement.getInstructions()) {
            switch (statement.getType()) {
                case ASM -> {
                    element.instruction(OpCode.INLINE_ASSEMBLY, builder -> builder
                            .stringOperand(statement.getArchitecture().name())
                            .stringOperand(instruction.getLine()));
                }
                case IR -> {

                }
            }
        }
        return null;
    }

    private InstructionReference emitWhileStatement(WhileStatement statement, ProgramElement element) {
        InstructionReference gotoLabel = instructionSet.createLabel(".w_goto", referenceId);
        InstructionReference endLabel = instructionSet.createLabel(".w_end", referenceId);
        InstructionReference conditionLabel = instructionSet.createLabel(".w_check", referenceId);

        this.currentContinueLabel = this.continueLabel;
        this.currentBrakeLabel = this.brakeLabel;

        InstructionReference conditionReference = generateStatement(statement.getCondition(), element);
        element.instruction(OpCode.MOVE, builder -> builder
                .referenceOperand(conditionLabel)
                .referenceOperand(conditionReference));

        continueLabel = gotoLabel;
        brakeLabel = endLabel;

        element.instruction(OpCode.LABEL, builder -> builder
                .referenceOperand(gotoLabel));

        element.instruction(OpCode.GOTO_IF, builder -> builder
                .referenceOperand(conditionLabel)
                .referenceOperand(endLabel));

        loopBodyStatement(statement.getBodyStatement(), element);

        element.instruction(OpCode.GOTO, builder -> builder
                .referenceOperand(gotoLabel));

        element.instruction(OpCode.LABEL, builder -> builder
                .referenceOperand(endLabel));

        brakeLabel = currentBrakeLabel;
        continueLabel = currentContinueLabel;

        return null;
    }

    private InstructionReference emitDoWhileStatement(DoWhileStatement statement, ProgramElement element) {
        InstructionReference gotoLabel = instructionSet.createLabel(".dw_goto", referenceId);
        InstructionReference endLabel = instructionSet.createLabel(".dw_end", referenceId);
        InstructionReference conditionLabel = instructionSet.createLabel(".dw_check", referenceId);

        this.currentContinueLabel = this.continueLabel;
        this.currentBrakeLabel = this.brakeLabel;

        continueLabel = gotoLabel;
        brakeLabel = endLabel;

        element.instruction(OpCode.LABEL, builder -> builder
                .referenceOperand(gotoLabel));

        loopBodyStatement(statement.getBodyStatement(), element);

        InstructionReference conditionReference = generateStatement(statement.getCondition(), element);
        element.instruction(OpCode.MOVE, builder -> builder
                .referenceOperand(conditionLabel) // todo check this cant move element to label?
                .referenceOperand(conditionReference));

        element.instruction(OpCode.GOTO_IF, builder -> builder
                .referenceOperand(conditionLabel)
                .referenceOperand(gotoLabel));

        element.instruction(OpCode.LABEL, builder -> builder
                .referenceOperand(endLabel));

        brakeLabel = currentBrakeLabel;
        continueLabel = currentContinueLabel;

        return null;
    }

    private InstructionReference emitLoopStatement(LoopStatement statement, ProgramElement element) {
        InstructionReference gotoLabel = instructionSet.createLabel(".loop_goto", referenceId);
        InstructionReference endLabel = instructionSet.createLabel(".loop_end", referenceId);

        this.currentLoopContinueLabel = this.loopContinueLabel;
        this.currentLoopBrakeLabel = this.loopBrakeLabel;

        loopContinueLabel = gotoLabel;
        loopBrakeLabel = endLabel;

        element.instruction(OpCode.LABEL, builder -> builder
                .referenceOperand(gotoLabel));

        loopBodyStatement(statement.getBodyStatement(), element);

        element.instruction(OpCode.GOTO, builder -> builder
                .referenceOperand(gotoLabel));

        element.instruction(OpCode.LABEL, builder -> builder
                .referenceOperand(endLabel));

        loopBrakeLabel = currentLoopBrakeLabel;
        loopContinueLabel = currentLoopContinueLabel;

        return null;
    }


    private InstructionReference emitForStatement(ForStatement statement, ProgramElement element) {
        if (statement.getCondition() instanceof ForStatement.NumberRangeCondition rangeCondition) {
            InstructionReference gotoLabel = instructionSet.createLabel(".f_goto", referenceId);
            InstructionReference loopLabel = instructionSet.createLabel(".f_b", referenceId);
            InstructionReference endLabel = instructionSet.createLabel(".f_end", referenceId);

            this.currentContinueLabel = this.continueLabel;
            this.currentBrakeLabel = this.brakeLabel;

            continueLabel = gotoLabel;
            brakeLabel = endLabel;

            this.generateStatement(rangeCondition.getStatement(), element);

            InstructionReference condition = this.generateStatement(rangeCondition.getCondition(), element);
            element.instruction(OpCode.GOTO_IF, builder -> builder
                    .referenceOperand(condition)
                    .referenceOperand(endLabel));

            element.instruction(OpCode.GOTO, builder -> builder
                    .referenceOperand(loopLabel));

            element.instruction(OpCode.LABEL, builder -> builder
                    .referenceOperand(gotoLabel));
            this.generateStatement(rangeCondition.getAppliedAction(), element);

            // condition get changed due to applied action!
            InstructionReference actionAppliedCondition = this.generateStatement(rangeCondition.getCondition(), element);
            element.instruction(OpCode.GOTO_IF, builder -> builder
                    .referenceOperand(actionAppliedCondition)
                    .referenceOperand(endLabel));

            element.instruction(OpCode.LABEL, builder -> builder
                    .referenceOperand(loopLabel));
            this.loopBodyStatement(statement.getBodyStatement(), element);

            element.instruction(OpCode.GOTO, builder -> builder
                    .referenceOperand(gotoLabel));

            element.instruction(OpCode.LABEL, builder -> builder
                    .referenceOperand(endLabel));

            brakeLabel = currentBrakeLabel;
            continueLabel = currentContinueLabel;
        }
        if (statement.getCondition() instanceof ForStatement.IterateCondition iterateCondition) {
            InstructionReference gotoLabel = instructionSet.createLabel(".f_goto", referenceId);
            InstructionReference loopLabel = instructionSet.createLabel(".f_b", referenceId);
            InstructionReference endLabel = instructionSet.createLabel(".f_end", referenceId);

            InstructionReference arrayReference = generateStatement(iterateCondition.getExpression(), element);
            InstructionReference elementReference = new InstructionReference(iterateCondition.getReference(), referenceId);

            InstructionReference index = instructionSet.createNumberReference(Type.I32, 0);

            this.currentContinueLabel = this.continueLabel;
            this.currentBrakeLabel = this.brakeLabel;

            continueLabel = gotoLabel;
            brakeLabel = endLabel;

            loadingElement:
            {
                element.instruction(OpCode.LABEL, builder -> builder
                        .referenceOperand(gotoLabel));

                element.instruction(OpCode.GOTO_IF, builder -> builder
                        //todo  .referenceOperand(ARRAY INDEX CHECK)
                        .referenceOperand(endLabel));

                element.instruction(OpCode.LOAD, builder -> builder
                        .referenceOperand(arrayReference)
                        .referenceOperand(index)
                        .referenceOperand(elementReference));

                element.instruction(OpCode.ADD, builder -> builder
                        .referenceOperand(index)
                        .numberOperand(Type.I32, 1));

                element.instruction(OpCode.GOTO, builder -> builder
                        .referenceOperand(loopLabel));
            }
            loopingBody:
            {
                element.instruction(OpCode.LABEL, builder -> builder
                        .referenceOperand(loopLabel));
                this.loopBodyStatement(statement.getBodyStatement(), element);

                element.instruction(OpCode.GOTO, builder -> builder
                        .referenceOperand(gotoLabel));
            }
            end:
            {
                element.instruction(OpCode.LABEL, builder -> builder
                        .referenceOperand(endLabel));
            }

            brakeLabel = currentBrakeLabel;
            continueLabel = currentContinueLabel;
        }
        return null;
    }

    private InstructionReference emitSwitchStatement(SwitchStatement statement, ProgramElement element) {
        boolean hasDefault = statement.hasDefaultCase();

        InstructionReference condition = this.generateStatement(statement.getCondition(), element);
        InstructionReference defaultCase = instructionSet.createLabel(".sw_df", referenceId);
        InstructionReference endLabel = instructionSet.createLabel(".sw_end", referenceId);
        InstructionReference[] gotoLabels = new InstructionReference[statement.getCases().length];

        for (int i = 0; i < statement.getCases().length; i++) {
            SwitchStatement.CaseElement caseElement = statement.getCases()[i];
            InstructionReference instructionReference = instructionSet.createLabel(".case_%s".formatted(i), referenceId);

            element.instruction(OpCode.LABEL, builder -> builder
                    .referenceOperand(instructionReference));
            this.generateStatement(caseElement.getBody(), element);

            gotoLabels[i] = instructionReference;
        }
        for (int i = 0; i < statement.getCases().length; i++) {
            SwitchStatement.CaseElement caseElement = statement.getCases()[i];

            for (Expression caseElementCondition : caseElement.getConditions()) {
                InstructionReference comparison = this.generateStatement(caseElementCondition, element);

                int index = i;
                element.instruction(OpCode.GOTO_IF, builder -> builder
                        .referenceOperand(comparison)
                        .referenceOperand(condition)
                        .referenceOperand(gotoLabels[index]));
            }
        }

        if (hasDefault) {
            element.instruction(OpCode.LABEL, builder -> builder
                    .referenceOperand(defaultCase));
            this.generateStatement(statement.getDefaultCase().getBody(), element);
        } else {
            element.instruction(OpCode.LABEL, builder -> builder
                    .referenceOperand(endLabel));
        }
        // TODO HANDLE DEFAULT CASE

        return null;
    }

    private InstructionReference emitMatchExpression(MatchExpression statement, ProgramElement element) {
        boolean hasDefault = statement.hasDefaultCase();
        InstructionReference proprietor = instructionSet.createDataReference(".match", statement.valuedType(), referenceId);

        InstructionReference condition = this.generateStatement(statement.getCondition(), element);

        InstructionReference defaultCase = instructionSet.createLabel(".match_df", referenceId);
        InstructionReference endLabel = instructionSet.createLabel(".match_end", referenceId);

        InstructionReference[] gotoLabels = new InstructionReference[statement.getCases().length];

        for (int i = 0; i < statement.getCases().length; i++) {
            MatchExpression.CaseElement caseElement = statement.getCases()[i];

            InstructionReference gotoLabel = instructionSet.createLabel(".match_case_%s".formatted(i), referenceId);
            gotoLabels[i] = gotoLabel;

            for (Expression caseElementCondition : caseElement.getConditions()) {
                InstructionReference comparison = this.generateStatement(caseElementCondition, element);

                element.instruction(OpCode.GOTO_IF, builder -> builder
                        .referenceOperand(condition)
                        .referenceOperand(comparison)
                        .referenceOperand(gotoLabel));
            }
        }
        for (int i = 0; i < gotoLabels.length; i++) {
            MatchExpression.CaseElement caseElement = statement.getCases()[i];

            InstructionReference gotoLabel = gotoLabels[i];
            InstructionReference currentReference = this.generateStatement(caseElement.getBody(), element);

            element.instruction(OpCode.LABEL, builder -> builder
                    .referenceOperand(gotoLabel));

            element.instruction(OpCode.MOVE, builder -> builder
                    .referenceOperand(proprietor)
                    .referenceOperand(currentReference));
        }

        if (hasDefault) {
            MatchExpression.CaseElement caseElement = statement.getDefaultCase();
            InstructionReference currentReference = generateStatement(caseElement.getBody(), element);

            element.instruction(OpCode.LABEL, builder -> builder
                    .referenceOperand(defaultCase));

            element.instruction(OpCode.MOVE, builder -> builder
                    .referenceOperand(proprietor)
                    .referenceOperand(currentReference));
        } else {
            element.instruction(OpCode.LABEL, builder -> builder
                    .referenceOperand(endLabel));
        }
        // TODO HANDLE DEFAULT CASE

        return proprietor;
    }


    private InstructionReference emitYieldStatement(YieldStatement statement, ProgramElement element) {
        return this.emitReturnStatement(statement, element); // todo change this only temporary
    }

    @SuppressWarnings("all")
    private InstructionReference emitFunctionType(FunctionStatement statement, ProgramElement element) {
        Reference reference = statement.getReference();// todo this.referenceStorage.getReferenceToStatement(statement);

        InstructionReference functionReference = new InstructionReference(reference, referenceId);
        element.instruction(OpCode.LABEL, builder -> {
            builder.referenceOperand(functionReference);

            for (Parameter parameter : statement.getParameters()) {
                InstructionReference instructionReference = instructionSet.createDataReference(".fun_par_%s".formatted(parameter.getName()),
                        parameter.getParsedType(), referenceId);

                if (parameter.getDefaultValue() != null) {
                    InstructionReference valueReference = generateStatement(parameter.getDefaultValue(), element);
                    element.instruction(OpCode.MOVE, builder2 -> builder2
                            .referenceOperand(instructionReference)
                            .referenceOperand(valueReference));
                }

                builder.referenceOperand(instructionReference);
            }
        });

        if (element.getType() == ProgramType.FUNCTION) {
            List<Reference> parameters = statement.getParameters().stream().map(parameter -> parameter.getReference()).toList();
            List<InstructionReference> references = new LinkedList<>();

            for (Reference parameter : parameters) {
                references.add(new InstructionReference(parameter, referenceId++));
            }

            element.setReference(functionReference);
            element.setParameters(references);
        }

        this.loopBodyStatement(statement.getBodyStatement(), element);
        return null;
    }

    private InstructionReference emitUDTDeclareStatement(UDTDeclareStatement statement, ProgramElement element) {

        return null;
    }

    private InstructionReference emitConstructStatement(ConstructStatement statement, ProgramElement element) {
        return null;
    }

    private InstructionReference emitStructureType(StructTypeStatement statement, ProgramElement element) {

        return null;
    }

    private InstructionReference emitEnumType(EnumTypeStatement enumType, ProgramElement element) {
        return null;
    }

    private void emitNamespaceStatement(NamespaceStatement statement, ProgramElement element) {

    }

    private InstructionReference emitClassType(ClassTypeStatement statement, ProgramElement element) {

        this.loopBodyStatement(statement.getBodyStatement(), element);
        return null;
    }

}
