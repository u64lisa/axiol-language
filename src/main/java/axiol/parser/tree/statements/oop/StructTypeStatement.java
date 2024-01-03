package axiol.parser.tree.statements.oop;

import axiol.parser.statement.Accessibility;
import axiol.parser.statement.Parameter;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.TokenPosition;
import axiol.parser.util.reference.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StructTypeStatement extends Statement {

    private final List<Parameter> entries;
    private final String name;
    private final Accessibility[] accessibility;

    private final Reference reference;

    public StructTypeStatement(List<Parameter> entries, String name, Accessibility[] accessibility, Reference reference, TokenPosition position) {
        this.entries = entries;
        this.name = name;
        this.accessibility = accessibility;
        this.reference = reference;
        this.position = position;
    }

    private final TokenPosition position;

    @Override
    public TokenPosition position() {
        return position;
    }

    @Override
    public NodeType type() {
        return NodeType.STRUCT_TYPE_STATEMENT;
    }

    @Override
    public List<Statement> childStatements() {
        return new ArrayList<>();
    }

    public Accessibility[] getAccessibility() {
        return accessibility;
    }

    public List<Parameter> getEntries() {
        return entries;
    }

    public Reference getReference() {
        return reference;
    }

    public String getName() {
        return name;
    }

}
