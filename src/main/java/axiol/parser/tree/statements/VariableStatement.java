package axiol.parser.tree.statements;

import axiol.parser.statement.Accessibility;
import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.TokenPosition;
import axiol.parser.util.reference.Reference;
import axiol.types.Type;

import java.util.List;

public class VariableStatement extends Statement {

    private final Accessibility[] access;
    private final String name;
    private final Type type;
    private final Expression value;

    private final Reference reference;

    public VariableStatement(String name, Type type, Expression value, Reference reference, TokenPosition position, Accessibility... access) {
        this.reference = reference;
        this.position = position;
        this.access = access;
        this.name = name;
        this.type = type;
        this.value = value;
    }

    private final TokenPosition position;

    @Override
    public TokenPosition position() {
        return position;
    }

    @Override
    public List<Statement> childStatements() {
        return List.of(value);
    }

    @Override
    public NodeType type() {
        return NodeType.VAR_STATEMENT;
    }

    public Accessibility[] getAccess() {
        return access;
    }

    public String getName() {
        return name;
    }

    public Reference getReference() {
        return reference;
    }

    public Type getType() {
        return type;
    }

    public Expression getValue() {
        return value;
    }
}
