package axiol.parser.tree.expressions.extra;

import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.TokenPosition;

import java.util.List;

public class ReferenceExpression extends Expression {

    private final Expression toReference;

    public ReferenceExpression(Expression toReference, TokenPosition position) {
        this.toReference = toReference;
        this.position = position;
    }

    private final TokenPosition position;

    @Override
    public TokenPosition position() {
        return position;
    }

    @Override
    public List<Statement> childStatements() {
        return List.of(toReference);
    }

    @Override
    public NodeType type() {
        return NodeType.REFERENCE_EXPR;
    }


    public Expression getToReference() {
        return toReference;
    }
}
