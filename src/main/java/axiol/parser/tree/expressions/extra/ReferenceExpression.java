package axiol.parser.tree.expressions.extra;

import axiol.parser.tree.Expression;

public class ReferenceExpression extends Expression {

    private final Expression toReference;

    public ReferenceExpression(Expression toReference) {
        this.toReference = toReference;
    }

    public Expression getToReference() {
        return toReference;
    }
}
