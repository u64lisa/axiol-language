package axiol.parser.tree.statements.control;

import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.tree.statements.BodyStatement;

import java.util.List;

public class LoopStatement extends Statement {

    private final BodyStatement bodyStatement;

    public LoopStatement(BodyStatement bodyStatement) {
        this.bodyStatement = bodyStatement;
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
