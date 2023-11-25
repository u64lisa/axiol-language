package axiol.parser.tree.expressions;

import axiol.parser.expression.Operator;
import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;

import java.util.List;

public class UnaryExpression extends Expression {

    private final Operator operator;
    private final Expression value;

    public UnaryExpression(Operator operator, Expression value) {
        this.operator = operator;
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public List<Statement> childStatements() {
        return List.of(value);
    }
}
