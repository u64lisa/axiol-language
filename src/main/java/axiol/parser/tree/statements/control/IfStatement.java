package axiol.parser.tree.statements.control;

import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.tree.statements.BodyStatement;
import axiol.parser.util.error.TokenPosition;

import java.util.ArrayList;
import java.util.List;

public class IfStatement extends Statement {

    private final Expression condition;
    private final BodyStatement body;
    private final Statement elseStatement; // body or more if

    public IfStatement(Expression condition, BodyStatement body, Statement elseStatement, TokenPosition position) {
        this.condition = condition;
        this.body = body;
        this.elseStatement = elseStatement;
        this.position = position;
    }

    private final TokenPosition position;

    @Override
    public TokenPosition position() {
        return position;
    }

    @Override
    public NodeType type() {
        return NodeType.IF_STATEMENT;
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
