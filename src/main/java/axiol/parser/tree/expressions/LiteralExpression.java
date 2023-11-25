package axiol.parser.tree.expressions;

import axiol.parser.tree.Expression;

public class LiteralExpression extends Expression {

    private final String path;

    public LiteralExpression(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
