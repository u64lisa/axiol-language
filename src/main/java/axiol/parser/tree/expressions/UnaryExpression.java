package axiol.parser.tree.expressions;

import axiol.parser.expression.Operator;
import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.TokenPosition;

import java.util.List;

public class UnaryExpression extends Expression {

    private final Operator operator;
    private final Expression value;

    public UnaryExpression(Operator operator, Expression value, TokenPosition position) {
        this.operator = operator;
        this.value = value;
        this.position = position;
    }

    private final TokenPosition position;

    @Override
    public TokenPosition position() {
        return position;
    }

    public Expression getValue() {
        return value;
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public NodeType type() {
        return NodeType.UNARY_EXPR;
    }


    @Override
    public List<Statement> childStatements() {
        return List.of(value);
    }
}
