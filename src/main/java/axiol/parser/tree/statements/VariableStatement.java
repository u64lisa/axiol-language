package axiol.parser.tree.statements;

import axiol.parser.statement.Accessibility;
import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.types.ParsedType;

import java.util.List;

public class VariableStatement extends Statement {

    private final Accessibility[] access;
    private final String name;
    private final ParsedType type;
    private final Expression value;

    public VariableStatement(String name, ParsedType type, Expression value, Accessibility... access) {
        this.access = access;
        this.name = name;
        this.type = type;
        this.value = value;
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

    public ParsedType getType() {
        return type;
    }

    public Expression getValue() {
        return value;
    }
}
