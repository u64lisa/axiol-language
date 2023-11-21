package axiol.parser.tree.expressions.sub;

import axiol.parser.tree.Expression;
import axiol.parser.util.error.Position;

public class BooleanExpression extends Expression {

    private final Position position;
    private final boolean value;

    public BooleanExpression(Position position, boolean value) {
        this.position = position;
        this.value = value;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isValue() {
        return value;
    }
}
