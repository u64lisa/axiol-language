package axiol.parser.tree.statements.oop;

import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.TokenPosition;
import axiol.types.ParsedType;

import java.util.ArrayList;
import java.util.List;

public class StructTypeStatement extends Statement {

    private final List<FieldEntry> entries;
    private final String name;

    public StructTypeStatement(List<FieldEntry> entries, String name, TokenPosition position) {
        this.entries = entries;
        this.name = name;
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

    public record FieldEntry(ParsedType valueType, String name) { }

    public List<FieldEntry> getEntries() {
        return entries;
    }

    public String getName() {
        return name;
    }

}
