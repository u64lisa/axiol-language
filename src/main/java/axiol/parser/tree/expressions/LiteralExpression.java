package axiol.parser.tree.expressions;

import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;

import java.util.ArrayList;
import java.util.List;

public class LiteralExpression extends Expression {

    private final String path;

    public LiteralExpression(String path) {
        this.path = path;
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
