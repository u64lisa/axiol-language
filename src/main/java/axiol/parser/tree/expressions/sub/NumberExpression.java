package axiol.parser.tree.expressions.sub;

import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.Position;

import java.util.ArrayList;
import java.util.List;

public class NumberExpression extends Expression {

    private final Position position;
    private final double numberValue;
    private final boolean signed;

    public NumberExpression(Position position, double numberValue, boolean signed) {
        this.position = position;
        this.numberValue = numberValue;
        this.signed = signed;
    }

    @Override
    public NodeType type() {
        return NodeType.NUMBER_EXPR;
    }


    @Override
    public List<Statement> childStatements() {
        return new ArrayList<>();
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
