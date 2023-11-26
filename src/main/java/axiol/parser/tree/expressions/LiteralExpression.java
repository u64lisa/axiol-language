package axiol.parser.tree.expressions;

import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.TokenPosition;

import java.util.ArrayList;
import java.util.List;

public class LiteralExpression extends Expression {

    private final String path;

    public LiteralExpression(String path, TokenPosition position) {
        this.path = path;
        this.position = position;
    }

    private final TokenPosition position;

    @Override
    public TokenPosition position() {
        return position;
    }

    public String getPath() {
        return path;
    }

    @Override
    public List<Statement> childStatements() {
        return new ArrayList<>();
    }

    @Override
    public NodeType type() {
        return NodeType.LITERAL_EXPR;
    }

}
