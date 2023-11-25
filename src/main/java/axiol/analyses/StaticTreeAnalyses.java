package axiol.analyses;

import axiol.parser.RootNodeProcessor;
import axiol.parser.tree.RootNode;
import axiol.parser.tree.Statement;
import axiol.parser.tree.expressions.ArrayInitExpression;
import axiol.parser.tree.expressions.BinaryExpression;
import axiol.parser.tree.expressions.LiteralExpression;
import axiol.parser.tree.expressions.UnaryExpression;
import axiol.parser.tree.expressions.control.MatchExpression;
import axiol.parser.tree.expressions.control.TernaryExpression;
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

public class StaticTreeAnalyses implements RootNodeProcessor {

    private final Class<? extends Statement>[] definitionStatements = new Class[]{
            ClassTypeStatement.class, FunctionStatement.class,
            StructTypeStatement.class, ConstructStatement.class,
            VariableStatement.class
    };
    private final Class<? extends Statement>[] declarationStatements = new Class[]{
            UDTDeclareStatement.class
    };
    private final Class<? extends Statement>[] controlFlowStatements = new Class[]{
            BreakStatement.class, ContinueStatement.class, DoWhileStatement.class,
            ForStatement.class, IfStatement.class, LoopStatement.class,
            ReturnStatement.class, SwitchStatement.class, UnreachableStatement.class,
            WhileStatement.class, YieldStatement.class
    };
    private final Class<? extends Statement>[] expressions = new Class[]{
            MatchExpression.class, TernaryExpression.class, ReferenceExpression.class,
            BooleanExpression.class, NumberExpression.class, StringExpression.class,
            ArrayInitExpression.class, BinaryExpression.class, UnaryExpression.class,
            LiteralExpression.class, FunctionStatement.class
    };
    private final Class<? extends Statement>[] specialCases = new Class[]{
            NativeStatement.class, LinkedNoticeStatement.class, BodyStatement.class
    };

    @Override
    public RootNode process(RootNode rootNode) {

        return rootNode;
    }

}
