package axiol.parser.tree.statements;

import axiol.parser.statement.Accessibility;
import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.TokenPosition;
import axiol.types.SimpleType;

import java.util.List;

public class VariableStatement extends Statement {

    private final Accessibility[] access;
    private final String name;
    private final SimpleType type;
    private final Expression value;

    public VariableStatement(String name, SimpleType type, Expression value, TokenPosition position, Accessibility... access) {
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

    public SimpleType getType() {
        return type;
    }

    public Expression getValue() {
        return value;
    }
}
