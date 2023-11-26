package axiol.parser.tree.statements.control;

import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.tree.statements.BodyStatement;
import axiol.parser.util.error.TokenPosition;

import java.util.List;

public class LoopStatement extends Statement {

    private final BodyStatement bodyStatement;

    public LoopStatement(BodyStatement bodyStatement, TokenPosition position) {
        this.bodyStatement = bodyStatement;
        this.position = position;
    }

    private final TokenPosition position;

    @Override
    public TokenPosition position() {
        return position;
    }

    public BodyStatement getBodyStatement() {
        return bodyStatement;
    }

    @Override
    public NodeType type() {
        return NodeType.LOOP_STATEMENT;
    }

    @Override
    public List<Statement> childStatements() {
        return List.of(bodyStatement);
    }
}
