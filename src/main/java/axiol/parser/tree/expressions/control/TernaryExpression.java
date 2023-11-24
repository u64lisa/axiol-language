package axiol.parser.tree.expressions.control;

import axiol.parser.tree.Expression;

public class TernaryExpression extends Expression {
    private final Expression ifTrue;
    private final Expression ifFalse;

    public TernaryExpression(Expression ifTrue, Expression ifFalse) {
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }

    public Expression getIfFalse() {
        return ifFalse;
    }

    public Expression getIfTrue() {
        return ifTrue;
    }
}
