package axiol.parser.tree.expressions.sub;

import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.Position;
import axiol.parser.util.error.TokenPosition;
import axiol.types.Type;

import java.util.ArrayList;
import java.util.List;

public class NumberExpression extends Expression {

    private final TokenPosition position;
    private final Number numberValue;
    private final Type type;
    private final boolean signed;

    public NumberExpression(TokenPosition position, double numberValue, Type type, boolean signed) {
        this.position = position;
        this.numberValue = numberValue;
        this.type = type;
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

    public Type getType() {
        return type;
    }

    @Override
    public List<Statement> childStatements() {
        return new ArrayList<>();
    }

    public boolean isSigned() {
        return signed;
    }

    public Number getNumberValue() {
        return numberValue;
    }
}
