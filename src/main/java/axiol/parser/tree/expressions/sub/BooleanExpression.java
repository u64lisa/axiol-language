package axiol.parser.tree.expressions.sub;

import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.Position;

import java.util.ArrayList;
import java.util.List;

public class BooleanExpression extends Expression {

    private final Position position;
    private final boolean value;

    public BooleanExpression(Position position, boolean value) {
        this.position = position;
        this.value = value;
    }

    @Override
    public List<Statement> childStatements() {
        return new ArrayList<>();
    }

    public Position getPosition() {
        return position;
    }

    public boolean isValue() {
        return value;
    }
}
