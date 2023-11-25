package axiol.parser.tree.statements.oop;

import axiol.parser.tree.Statement;
import axiol.types.ParsedType;

import java.util.ArrayList;
import java.util.List;

public class StructTypeStatement extends Statement {

    private final List<FieldEntry> entries;
    private final String name;

    public StructTypeStatement(List<FieldEntry> entries, String name) {
        this.entries = entries;
        this.name = name;
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
