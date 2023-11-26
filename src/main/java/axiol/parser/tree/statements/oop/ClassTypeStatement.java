package axiol.parser.tree.statements.oop;

import axiol.parser.statement.Accessibility;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.tree.statements.BodyStatement;

import java.util.List;

public class ClassTypeStatement extends Statement {

    private final Accessibility[] accessibility;
    private final String name;
    private final String parent;
    private final BodyStatement bodyStatement;

    public ClassTypeStatement(Accessibility[] accessibility, String name, String parent, BodyStatement bodyStatement) {
        this.accessibility = accessibility;
        this.name = name;
        this.parent = parent;
        this.bodyStatement = bodyStatement;
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

    public BodyStatement getBodyStatement() {
        return bodyStatement;
    }
}
