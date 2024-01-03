package axiol.parser.tree.expressions.extra;

import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.TokenPosition;
import axiol.types.SimpleType;

import java.util.List;

public class CastExpression extends Expression {
    private final TokenPosition tokenPosition;
    private final SimpleType castTo;
    private final Expression value;

    public CastExpression(TokenPosition tokenPosition, SimpleType castTo, Expression value) {
        this.tokenPosition = tokenPosition;
        this.castTo = castTo;
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }

    public SimpleType getCastTo() {
        return castTo;
    }

    @Override
    public SimpleType valuedType() {
        return castTo;
    }

    @Override
    public List<Statement> childStatements() {
        return List.of(value);
    }

    @Override
    public NodeType type() {
        return NodeType.CAST_EXPR;
    }

    @Override
    public TokenPosition position() {
        return tokenPosition;
    }
}
