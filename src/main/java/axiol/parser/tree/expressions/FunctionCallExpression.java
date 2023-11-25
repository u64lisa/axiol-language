package axiol.parser.tree.expressions;

import axiol.parser.tree.Expression;

import java.util.List;

public class FunctionCallExpression extends Expression {

    private final String pathToFunction;
    private final List<Expression> parameters;

    public FunctionCallExpression(String pathToFunction, List<Expression> parameters) {
        this.pathToFunction = pathToFunction;
        this.parameters = parameters;
    }

    public List<Expression> getParameters() {
        return parameters;
    }

    public String getPathToFunction() {
        return pathToFunction;
    }
}
