package axiol.parser.tree.expressions;

import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;

import java.util.ArrayList;
import java.util.List;

public class CallExpression extends Expression {

    private final String pathToFunction;
    private final List<Expression> parameters;

    public CallExpression(String pathToFunction, List<Expression> parameters) {
        this.pathToFunction = pathToFunction;
        this.parameters = parameters;
    }

    public List<Expression> getParameters() {
        return parameters;
    }

    public String getPathToFunction() {
        return pathToFunction;
    }

    @Override
    public List<Statement> childStatements() {
        return new ArrayList<>(parameters);
    }

    @Override
    public NodeType type() {
        return NodeType.CALL_EXPR;
    }

}
