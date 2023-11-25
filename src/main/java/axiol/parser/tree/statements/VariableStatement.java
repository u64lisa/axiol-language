package axiol.parser.tree.statements;

import axiol.parser.statement.Accessibility;
import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;
import axiol.types.ParsedType;

import java.util.List;

public class VariableStatement extends Statement {

    private final Accessibility[] access;
    private final String name;
    private final ParsedType type;
    private final Expression value;
    private final boolean pointer;

    public VariableStatement(String name, ParsedType type, Expression value, boolean pointer, Accessibility... access) {
        this.pointer = pointer;
        this.access = access;
        this.name = name;
        this.type = type;
        this.value = value;
    }

    @Override
    public List<Statement> childStatements() {
        return List.of(value);
    }

    public boolean isPointer() {
        return pointer;
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
