package axiol.parser.tree.statements.control;

import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.TokenPosition;

import java.util.ArrayList;
import java.util.List;

public class BreakStatement extends Statement {
    public BreakStatement(TokenPosition position) {
        this.position = position;
    }

    @Override
    public List<Statement> childStatements() {
        return new ArrayList<>();
    }

    @Override
    public NodeType type() {
        return NodeType.BREAK_STATEMENT;
    }

    private final TokenPosition position;

    @Override
    public TokenPosition position() {
        return position;
    }

}
