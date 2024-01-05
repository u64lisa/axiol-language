package axiol.parser.tree.expressions;

import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.TokenPosition;
import axiol.parser.util.reference.Reference;
import axiol.types.Type;

import java.util.ArrayList;
import java.util.List;

public class LiteralExpression extends Expression {

    private final Reference reference;
    private final String path;

    public LiteralExpression(Reference reference, String path, TokenPosition position) {
        this.reference = reference;
        this.path = path;
        this.position = position;
    }

    private final TokenPosition position;

    @Override
    public TokenPosition position() {
        return position;
    }

    public Reference getReference() {
        return reference;
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

    @Override
    public Type valuedType() {
        return reference.getValueType();
    }
}
