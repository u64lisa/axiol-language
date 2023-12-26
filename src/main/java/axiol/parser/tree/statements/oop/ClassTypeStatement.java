package axiol.parser.tree.statements.oop;

import axiol.parser.statement.Accessibility;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.tree.statements.BodyStatement;
import axiol.parser.util.error.TokenPosition;
import axiol.types.Reference;

import java.util.List;
import java.util.UUID;

public class ClassTypeStatement extends Statement {

    private final Accessibility[] accessibility;
    private final String name;
    private final String parent;
    private final BodyStatement bodyStatement;
    private final UUID uuid;

    private final Reference reference;

    public ClassTypeStatement(Accessibility[] accessibility, String name, String parent, BodyStatement bodyStatement, UUID uuid, Reference reference, TokenPosition position) {
        this.accessibility = accessibility;
        this.name = name;
        this.parent = parent;
        this.bodyStatement = bodyStatement;
        this.uuid = uuid;
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
        return NodeType.CLASS_TYPE_STATEMENT;
    }

    @Override
    public List<Statement> childStatements() {
        return List.of(bodyStatement);
    }

    public Accessibility[] getAccessibility() {
        return accessibility;
    }

    public String getName() {
        return name;
    }

    public String getParent() {
        return parent;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Reference getReference() {
        return reference;
    }

    public BodyStatement getBodyStatement() {
        return bodyStatement;
    }
}
