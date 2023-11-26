package axiol.parser.tree.expressions.sub;

import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.Position;
import axiol.parser.util.error.TokenPosition;

import java.util.ArrayList;
import java.util.List;

public class NumberExpression extends Expression {

    private final TokenPosition position;
    private final double numberValue;
    private final boolean signed;

    public NumberExpression(TokenPosition position, double numberValue, boolean signed) {
        this.position = position;
        this.numberValue = numberValue;
        this.signed = signed;
    }

    @Override
    public TokenPosition position() {
        return position;
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

    public double getNumberValue() {
        return numberValue;
    }
}
