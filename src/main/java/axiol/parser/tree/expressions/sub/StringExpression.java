package axiol.parser.tree.expressions.sub;

import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.Position;

import java.util.ArrayList;
import java.util.List;

public class StringExpression extends Expression {

    private final Position position;
    private final String value;

    public StringExpression(Position position, String value) {
        this.position = position;
        this.value = value;
    }

    @Override
    public List<Statement> childStatements() {
        return new ArrayList<>();
    }

    @Override
    public NodeType type() {
        return NodeType.STRING_EXPR;
    }


    public Position getPosition() {
        return position;
    }

    public String getValue() {
        return value;
    }
}
