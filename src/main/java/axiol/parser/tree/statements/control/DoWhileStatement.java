package axiol.parser.tree.statements.control;

import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.tree.statements.BodyStatement;

import java.util.List;

public class DoWhileStatement extends Statement {
    private final Expression condition;
    private final BodyStatement bodyStatement;

    public DoWhileStatement(Expression condition, BodyStatement bodyStatement) {
        this.condition = condition;
        this.bodyStatement = bodyStatement;
    }

    @Override
    public NodeType type() {
        return NodeType.DO_WHILE_STATEMENT;
    }

    @Override
    public List<Statement> childStatements() {
        return List.of(condition, bodyStatement);
    }

    public Expression getCondition() {
        return condition;
    }

    public BodyStatement getBodyStatement() {
        return bodyStatement;
    }
}
