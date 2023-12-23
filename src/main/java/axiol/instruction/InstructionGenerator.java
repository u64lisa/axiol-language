package axiol.instruction;

import axiol.parser.tree.RootNode;
import axiol.parser.tree.Statement;
import axiol.parser.tree.expressions.*;
import axiol.parser.tree.expressions.control.MatchExpression;
import axiol.parser.tree.expressions.extra.ReferenceExpression;
import axiol.parser.tree.expressions.sub.BooleanExpression;
import axiol.parser.tree.expressions.sub.NumberExpression;
import axiol.parser.tree.expressions.sub.StringExpression;
import axiol.parser.tree.statements.BodyStatement;
import axiol.parser.tree.statements.VariableStatement;
import axiol.parser.tree.statements.control.*;
import axiol.parser.tree.statements.oop.*;
import axiol.parser.tree.statements.special.NativeStatement;
import axiol.types.ReferenceStorage;

public class InstructionGenerator {
    private final InstructionSetBuilder instructionSet;
    private ReferenceStorage referenceStorage;

    private int referenceId = 0;

    public InstructionGenerator() {
        instructionSet = new InstructionSetBuilder();
    }


    //@formatter:off
    public InstructionSet emit(RootNode rootNode) {
        this.referenceStorage = rootNode.getReferences();

        for (Statement statement : rootNode.getStatements()) {
            switch (statement.type()) {
                case CLASS_TYPE_STATEMENT ->  emitClassType((ClassTypeStatement) statement);
                case STRUCT_TYPE_STATEMENT -> emitStructureType((StructTypeStatement) statement);
                case FUNCTION_STATEMENT ->    emitFunctionType((FunctionStatement) statement);
                case VAR_STATEMENT ->         emitVariable((VariableStatement) statement);
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
            switch (statement.type()) {
                // body
                case BODY_STATEMENT ->        loopBodyStatement((BodyStatement) statement);
                // inner body statements
                case NATIVE_STATEMENT ->      emitNativeStatement(      (NativeStatement)      statement);
                case YIELD_STATEMENT ->       emitYieldStatement(       (YieldStatement)       statement);
                case WHILE_STATEMENT ->       emitWhileStatement(       (WhileStatement)       statement);
                case LOOP_STATEMENT ->        emitLoopStatement(        (LoopStatement)        statement);
                case UNREACHABLE_STATEMENT -> emitUnreachableStatement( (UnreachableStatement) statement);
                case RETURN_STATEMENT ->      emitReturnStatement(      (ReturnStatement)      statement);
                case SWITCH_STATEMENT ->      emitSwitchStatement(      (SwitchStatement)      statement);
                case IF_STATEMENT ->          emitIfStatement(          (IfStatement)          statement);
                case FOR_STATEMENT ->         emitForStatement(         (ForStatement)         statement);
                case DO_WHILE_STATEMENT ->    emitDoWhileStatement(     (DoWhileStatement)     statement);
                case CONTINUE_STATEMENT ->    emitContinueStatement(    (ContinueStatement)    statement);
                case CONSTRUCT_STATEMENT ->   emitConstructStatement(   (ConstructStatement)   statement);
                case BREAK_STATEMENT ->       emitBreakStatement(       (BreakStatement)       statement);
                case UDT_DECLARE_STATEMENT -> emitUDTDeclareStatement(  (UDTDeclareStatement)  statement);
                case VAR_STATEMENT ->         emitVarStatement(         (VariableStatement)    statement);
                case STRUCT_TYPE_STATEMENT -> emitStructureType(        (StructTypeStatement)  statement);
                case FUNCTION_STATEMENT ->    emitFunctionType(         (FunctionStatement)    statement);
                case CLASS_TYPE_STATEMENT ->  emitClassType(            (ClassTypeStatement)   statement);
                // expressions
                case ARRAY_EXPR ->            emitArrayExpression(      (ArrayInitExpression) statement);
                case CALL_EXPR ->             emitCallExpression(       (CallExpression)      statement);
                case LITERAL_EXPR ->          emitLiteralExpression(    (LiteralExpression)   statement);
                case UNARY_EXPR ->            emitUnaryExpression(      (UnaryExpression)     statement);
                case BINARY_EXPR ->           emitBinaryExpression(     (BinaryExpression)    statement);
                case STRING_EXPR ->           emitStringExpression(     (StringExpression)    statement);
                case NUMBER_EXPR ->           emitNumberExpression(     (NumberExpression)    statement);
                case BOOLEAN_EXPR ->          emitBooleanExpression(    (BooleanExpression)   statement);
                case REFERENCE_EXPR ->        emitReferenceExpression(  (ReferenceExpression) statement);
                case MATCH_EXPR ->            emitMatchExpression(      (MatchExpression)     statement);
                
                // linking, root
                default -> throw new IllegalArgumentException("unexpected statement '%s' at body!"
                        .formatted(statement.type().name()));
            }
        }
    }
    //@formatter:on

    private void emitMatchExpression(MatchExpression statement) {
    }

    private void emitReferenceExpression(ReferenceExpression statement) {
    }

    private void emitBooleanExpression(BooleanExpression statement) {
    }

    private void emitNumberExpression(NumberExpression statement) {
    }

    private void emitStringExpression(StringExpression statement) {
    }

    private void emitBinaryExpression(BinaryExpression statement) {
    }

    private void emitUnaryExpression(UnaryExpression statement) {
    }

    private void emitLiteralExpression(LiteralExpression statement) {
    }

    private void emitCallExpression(CallExpression statement) {
    }

    private void emitArrayExpression(ArrayInitExpression statement) {
    }

    private void emitSwitchStatement(SwitchStatement statement) {
    }

    private void emitVarStatement(VariableStatement statement) {
    }

    private void emitUDTDeclareStatement(UDTDeclareStatement statement) {
    }

    private void emitBreakStatement(BreakStatement statement) {
    }

    private void emitConstructStatement(ConstructStatement statement) {
    }

    private void emitContinueStatement(ContinueStatement statement) {
    }

    private void emitIfStatement(IfStatement statement) {
    }

    private void emitForStatement(ForStatement statement) {
    }

    private void emitDoWhileStatement(DoWhileStatement statement) {
    }

    private void emitReturnStatement(ReturnStatement statement) {
    }

    private void emitUnreachableStatement(UnreachableStatement statement) {
    }

    private void emitNativeStatement(NativeStatement statement) {
    }

    private void emitWhileStatement(WhileStatement statement) {
    }

    private void emitLoopStatement(LoopStatement statement) {
    }

    private void emitYieldStatement(YieldStatement statement) {
    }

    private void emitVariable(VariableStatement statement) {
    }

    private void emitFunctionType(FunctionStatement statement) {
    }

    private void emitStructureType(StructTypeStatement statement) {
    }

    private void emitClassType(ClassTypeStatement statement) {
    }

}
