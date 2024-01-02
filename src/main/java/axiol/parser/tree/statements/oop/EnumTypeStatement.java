package axiol.parser.tree.statements.oop;

import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.TokenPosition;

import java.util.ArrayList;
import java.util.List;

public class EnumTypeStatement extends Statement {
    private final TokenPosition position;
    private final List<String> elements;

    public EnumTypeStatement(TokenPosition position, List<String> elements) {
        this.position = position;
        this.elements = elements;
    }

    public List<String> getElements() {
        return elements;
    }

    @Override
    public List<Statement> childStatements() {
        return new ArrayList<>();
    }

    @Override
    public NodeType type() {
        return NodeType.ENUM_TYPE_STATEMENT;
    }

    @Override
    public TokenPosition position() {
        return position;
    }
}
