package axiol.parser.tree.statements.control;

import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;
import axiol.parser.tree.statements.BodyStatement;

import java.util.ArrayList;
import java.util.List;

public class IfStatement extends Statement {

    private final Expression condition;
    private final BodyStatement body;
    private final Statement elseStatement; // body or more if

    public IfStatement(Expression condition, BodyStatement body, Statement elseStatement) {
        this.condition = condition;
        this.body = body;
        this.elseStatement = elseStatement;
    }

    @Override
    public List<Statement> childStatements() {
        return List.of(condition, body, elseStatement);
    }

    public Expression getCondition() {
        return condition;
    }

    public BodyStatement getBody() {
        return body;
    }

    public Statement getElseStatement() {
        return elseStatement;
    }
}
