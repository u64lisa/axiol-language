package axiol.parser.tree.statements;

import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.TokenPosition;

import java.util.List;

public class BodyStatement extends Statement {

    private final TokenPosition tokenPosition;
    private final List<Statement> statements;

    public BodyStatement(TokenPosition tokenPosition, List<Statement> statements) {
        this.tokenPosition = tokenPosition;
        this.statements = statements;
    }

    @Override
    public NodeType type() {
        return NodeType.BODY_STATEMENT;
    }

    @Override
    public TokenPosition position() {
        return tokenPosition;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public List<Statement> childStatements() {
        return statements;
    }
}
