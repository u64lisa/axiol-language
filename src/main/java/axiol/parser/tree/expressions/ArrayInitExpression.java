package axiol.parser.tree.expressions;

import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.TokenPosition;
import axiol.types.SimpleType;

import java.util.ArrayList;
import java.util.List;

public class ArrayInitExpression extends Expression {
    private final List<Expression> values;
    private final SimpleType simpleType;
    private final Expression initSize;

    public ArrayInitExpression(List<Expression> values, SimpleType simpleType, Expression initSize, TokenPosition position) {
        this.values = values;
        this.simpleType = simpleType;
        this.initSize = initSize;
        this.position = position;
    }

    private final TokenPosition position;

    @Override
    public TokenPosition position() {
        return position;
    }

    @Override
    public List<Statement> childStatements() {
        List<Statement> statements = new ArrayList<>(values);
        statements.add(initSize);
        return statements;
    }

    @Override
    public NodeType type() {
        return NodeType.ARRAY_EXPR;
    }


    public Expression getInitSize() {
        return initSize;
    }

    public List<Expression> getValues() {
        return values;
    }

    @Override
    public SimpleType valuedType() {
        return simpleType;
    }
}
