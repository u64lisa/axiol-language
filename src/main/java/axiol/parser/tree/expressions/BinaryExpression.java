package axiol.parser.tree.expressions;

import axiol.parser.expression.Operator;
import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.TokenPosition;
import axiol.types.Type;

import java.util.List;

public class BinaryExpression extends Expression {

    private final Operator operator;
    private final Expression leftAssociate;
    private final Expression rightAssociate;

    public BinaryExpression(Operator operator, Expression leftAssociate, Expression rightAssociate, TokenPosition position) {
        this.operator = operator;
        this.leftAssociate = leftAssociate;
        this.rightAssociate = rightAssociate;
        this.position = position;
    }

    private final TokenPosition position;

    @Override
    public TokenPosition position() {
        return position;
    }

    @Override
    public NodeType type() {
        return NodeType.BINARY_EXPR;
    }

    @Override
    public Type valuedType() {
        return leftAssociate.valuedType();
    }

    @Override
    public List<Statement> childStatements() {
        return List.of(leftAssociate, rightAssociate);
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
