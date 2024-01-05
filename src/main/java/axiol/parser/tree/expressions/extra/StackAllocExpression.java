package axiol.parser.tree.expressions.extra;

import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.tree.expressions.sub.NumberExpression;
import axiol.parser.util.error.TokenPosition;
import axiol.types.Type;

import java.util.List;

public class StackAllocExpression extends Expression {

    private final TokenPosition tokenPosition;
    private final Type type;
    private final NumberExpression depth;

    public StackAllocExpression(TokenPosition tokenPosition, Type type, NumberExpression depth) {
        this.tokenPosition = tokenPosition;
        this.type = type;
        this.depth = depth;
    }

    public TokenPosition getTokenPosition() {
        return tokenPosition;
    }

    public NumberExpression getDepth() {
        return depth;
    }

    public Type getSimpleType() {
        return type;
    }

    @Override
    public Type valuedType() {
        return type;
    }

    @Override
    public List<Statement> childStatements() {
        return List.of(depth);
    }

    @Override
    public NodeType type() {
        return NodeType.STACK_ALLOC;
    }

    @Override
    public TokenPosition position() {
        return tokenPosition;
    }
}
