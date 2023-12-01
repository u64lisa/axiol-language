package axiol.parser.tree;

import axiol.parser.util.error.TokenPosition;

import java.util.List;

public abstract class Statement {
    public String scopePosition = null;

    public abstract List<Statement> childStatements();

    public abstract NodeType type();

    public abstract TokenPosition position();

}
