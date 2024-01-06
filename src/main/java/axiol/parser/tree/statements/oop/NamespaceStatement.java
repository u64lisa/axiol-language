package axiol.parser.tree.statements.oop;

import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.tree.statements.BodyStatement;
import axiol.parser.util.error.TokenPosition;
import axiol.parser.util.reference.Reference;

import java.util.List;

public class NamespaceStatement extends Statement {

    private final TokenPosition tokenPosition;
    private final Reference namespace;
    private final BodyStatement bodyStatement;

    public NamespaceStatement(TokenPosition tokenPosition, Reference namespace, BodyStatement bodyStatement) {
        this.tokenPosition = tokenPosition;
        this.namespace = namespace;

        this.bodyStatement = bodyStatement;
    }

    @Override
    public List<Statement> childStatements() {
        return bodyStatement.childStatements();
    }

    @Override
    public NodeType type() {
        return NodeType.NAMESPACE_STATEMENT;
    }

    @Override
    public TokenPosition position() {
        return tokenPosition;
    }

    public BodyStatement getBodyStatement() {
        return bodyStatement;
    }

    public Reference getNamespace() {
        return namespace;
    }

    public TokenPosition getTokenPosition() {
        return tokenPosition;
    }
}
