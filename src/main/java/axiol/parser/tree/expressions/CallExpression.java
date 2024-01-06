package axiol.parser.tree.expressions;

import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.TokenPosition;
import axiol.parser.util.reference.Reference;
import axiol.types.Type;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.List;

public class CallExpression extends Expression {

    private final Reference reference;
    private final List<Expression> parameters;

    public CallExpression(Reference reference, List<Expression> parameters, TokenPosition position) {
        this.reference = reference;
        this.parameters = parameters;
        this.position = position;
    }

    private final TokenPosition position;

    @Override
    public TokenPosition position() {
        return position;
    }

    public List<Expression> getParameters() {
        return parameters;
    }

    @Override
    public List<Statement> childStatements() {
        return new ArrayList<>();
    }

    @Override
    public NodeType type() {
        return NodeType.CALL_EXPR;
    }

    public Reference getReference() {
        return reference;
    }

    @Override
    public Type valuedType() {
        return reference.getValueType();
    }
}
