package axiol.parser.tree.expressions.sub;

import axiol.parser.util.error.Position;

public class NumberExpression extends Exception {

    private final Position position;
    private final long numberValue;

    public NumberExpression(Position position, long numberValue) {
        this.position = position;
        this.numberValue = numberValue;
    }

    public Position getPosition() {
        return position;
    }

    public long getNumberValue() {
        return numberValue;
    }
}
