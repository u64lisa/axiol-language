package axiol.instruction;

import axiol.parser.tree.RootNode;
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
import axiol.types.ScopeVariable;

import java.util.List;

public class InstructionGenerator extends TreeVisitor<InstructionSet> {

    @Override
    public InstructionSet processNewReturn(RootNode rootNode) {
        super.processNewReturn(rootNode);

        return null; // todo
    }

    @Override
    public void visitClassType(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, ClassTypeStatement statement) {

    }

    @Override
    public void visitFunction(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, FunctionStatement statement) {

    }

    @Override
    public void visitStructureType(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, StructTypeStatement statement) {

    }

    @Override
    public void visitConstruct(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, ConstructStatement statement) {

    }

    @Override
    public void visitVariable(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, VariableStatement statement) {

    }

    @Override
    public void visitLinkedNotice(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, LinkedNoticeStatement statement) {

    }

    @Override
    public void visitUDTDeclaration(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, UDTDeclareStatement udtDeclareStatement) {

    }

    @Override
    public void visitBreak(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, BreakStatement statement) {

    }

    @Override
    public void visitContinue(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, ContinueStatement statement) {

    }

    @Override
    public void visitFor(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, ForStatement statement) {

    }

    @Override
    public void visitDoWhile(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, DoWhileStatement statement) {

    }

    @Override
    public void visitIf(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, IfStatement statement) {

    }

    @Override
    public void visitLoop(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, LoopStatement statement) {

    }

    @Override
    public void visitReturn(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, ReturnStatement statement) {

    }

    @Override
    public void visitSwitch(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, SwitchStatement statement) {

    }

    @Override
    public void visitUnreachable(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, UnreachableStatement statement) {

    }

    @Override
    public void visitWhile(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, WhileStatement statement) {

    }

    @Override
    public void visitYield(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, YieldStatement statement) {

    }

    @Override
    public void visitBody(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, BodyStatement statement) {

    }

    @Override
    public void visitNative(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, NativeStatement statement) {

    }

    @Override
    public void visitMatch(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, MatchExpression statement) {

    }

    @Override
    public void visitReference(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, ReferenceExpression statement) {

    }

    @Override
    public void visitBoolean(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, BooleanExpression statement) {

    }

    @Override
    public void visitNumber(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, NumberExpression statement) {

    }

    @Override
    public void visitString(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, StringExpression statement) {

    }

    @Override
    public void visitArray(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, ArrayInitExpression statement) {

    }

    @Override
    public void visitBinary(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, BinaryExpression statement) {

    }

    @Override
    public void visitUnary(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, UnaryExpression statement) {

    }

    @Override
    public void visitLiteral(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, LiteralExpression statement) {

    }

    @Override
    public void visitCall(SourceFile sourceFile, String scope, List<ScopeVariable> scopeVars, CallExpression statement) {

    }
}
