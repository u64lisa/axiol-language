package axiol.parser.tree.expressions.sub;

import axiol.parser.tree.Expression;
import axiol.parser.util.error.Position;

public class NumberExpression extends Expression {

    private final Position position;
    private final double numberValue;
    private final boolean signed;

    public NumberExpression(Position position, double numberValue, boolean signed) {
        this.position = position;
        this.numberValue = numberValue;
        this.signed = signed;
    }

    public boolean isSigned() {
        return signed;
    }

    public Position getPosition() {
        return position;
    }

    public double getNumberValue() {
        return numberValue;
    }
}
