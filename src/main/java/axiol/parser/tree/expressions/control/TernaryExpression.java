package axiol.parser.tree.expressions.control;

import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;

import java.util.List;

public class TernaryExpression extends Expression {
    private final Expression ifTrue;
    private final Expression ifFalse;

    public TernaryExpression(Expression ifTrue, Expression ifFalse) {
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }

    @Override
    public List<Statement> childStatements() {
        return List.of(ifTrue, ifFalse);
    }

    public Expression getIfFalse() {
        return ifFalse;
    }

    public Expression getIfTrue() {
        return ifTrue;
    }
}
