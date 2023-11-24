package axiol.parser.tree.expressions;

import axiol.parser.tree.Expression;

import java.util.List;

public class ArrayInitExpression extends Expression {
    private final List<Expression> values;
    private final Expression initSize;

    public ArrayInitExpression(List<Expression> values, Expression initSize) {
        this.values = values;
        this.initSize = initSize;
    }

    public Expression getInitSize() {
        return initSize;
    }

    public List<Expression> getValues() {
        return values;
    }
}
