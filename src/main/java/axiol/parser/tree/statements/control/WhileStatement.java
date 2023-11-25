package axiol.parser.tree.statements.control;

import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;
import axiol.parser.tree.statements.BodyStatement;

import java.util.List;

public class WhileStatement extends Statement {
    private final Expression condition;
    private final BodyStatement bodyStatement;

    public WhileStatement(Expression condition, BodyStatement bodyStatement) {
        this.condition = condition;
        this.bodyStatement = bodyStatement;
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
