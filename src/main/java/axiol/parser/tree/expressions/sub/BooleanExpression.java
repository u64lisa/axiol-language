package axiol.parser.tree.expressions.sub;

import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.TokenPosition;
import axiol.types.Type;

import java.util.ArrayList;
import java.util.List;

public class BooleanExpression extends Expression {

    private final TokenPosition position;
    private final boolean value;

    public BooleanExpression(TokenPosition position, boolean value) {
        this.position = position;
        this.value = value;
    }

    @Override
    public TokenPosition position() {
        return position;
    }

    @Override
    public List<Statement> childStatements() {
        return new ArrayList<>();
    }

    @Override
    public NodeType type() {
        return NodeType.BOOLEAN_EXPR;
    }

    public boolean isValue() {
        return value;
    }

    @Override
    public Type valuedType() {
        return Type.BOOLEAN;
    }
}
