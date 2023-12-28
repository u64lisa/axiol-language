package axiol.parser.tree.statements;

import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.TokenPosition;

import java.util.List;

public class EmptyStatement extends Statement {

    @Override
    public List<Statement> childStatements() {
        return null;
    }

    @Override
    public NodeType type() {
        return NodeType.EMPTY;
    }

    @Override
    public TokenPosition position() {
        return null;
    }
}
