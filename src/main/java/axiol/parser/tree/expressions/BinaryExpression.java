package axiol.parser.tree.expressions;

import axiol.parser.expression.Operator;
import axiol.parser.tree.Expression;

public class BinaryExpression extends Expression {

    private final Operator operator;
    private final Expression leftAssociate;
    private final Expression rightAssociate;

    public BinaryExpression(Operator operator, Expression leftAssociate, Expression rightAssociate) {
        this.operator = operator;
        this.leftAssociate = leftAssociate;
        this.rightAssociate = rightAssociate;
    }

    public Operator getOperator() {
        return operator;
    }

    public Expression getLeftAssociate() {
        return leftAssociate;
    }

    public Expression getRightAssociate() {
        return rightAssociate;
    }
}
