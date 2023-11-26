package axiol.parser.tree.statements.control;

import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.TokenPosition;

import java.util.List;

public class ReturnStatement extends Statement {

    private final Expression value;

    public ReturnStatement(Expression value, TokenPosition position) {
        this.value = value;
        this.position = position;
    }

    private final TokenPosition position;

    @Override
    public TokenPosition position() {
        return position;
    }

    @Override
    public NodeType type() {
        return NodeType.RETURN_STATEMENT;
    }

    @Override
    public List<Statement> childStatements() {
        return List.of(value);
    }

    public Expression getValue() {
        return value;
    }
}
