package axiol.parser.tree.statements.control;

import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.TokenPosition;

import java.util.ArrayList;
import java.util.List;

public class ContinueStatement extends Statement {
    public ContinueStatement(TokenPosition position) {
        this.position = position;
    }

    @Override
    public List<Statement> childStatements() {
        return new ArrayList<>();
    }

    @Override
    public NodeType type() {
        return NodeType.CONTINUE_STATEMENT;
    }

    private final TokenPosition position;

    @Override
    public TokenPosition position() {
        return position;
    }

}
