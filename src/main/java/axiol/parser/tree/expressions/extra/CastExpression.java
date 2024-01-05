package axiol.parser.tree.expressions.extra;

import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.TokenPosition;
import axiol.types.Type;

import java.util.List;

public class CastExpression extends Expression {
    private final TokenPosition tokenPosition;
    private final Type castTo;
    private final Expression value;

    public CastExpression(TokenPosition tokenPosition, Type castTo, Expression value) {
        this.tokenPosition = tokenPosition;
        this.castTo = castTo;
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }

    public Type getCastTo() {
        return castTo;
    }

    @Override
    public Type valuedType() {
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
