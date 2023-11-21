package axiol.parser.tree.expressions.sub;

import axiol.parser.tree.Expression;
import axiol.parser.util.error.Position;

public class StringExpression extends Expression {

    private final Position position;
    private final String value;

    public StringExpression(Position position, String value) {
        this.position = position;
        this.value = value;
    }

    public Position getPosition() {
        return position;
    }

    public String getValue() {
        return value;
    }
}
