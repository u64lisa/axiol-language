package axiol.parser.tree;

import axiol.parser.scope.ScopeStash;
import axiol.parser.util.SourceFile;
import axiol.parser.util.error.TokenPosition;

import java.util.ArrayList;
import java.util.List;

public class RootNode extends Statement {

    private final SourceFile sourceFile;
    private final ScopeStash scopeStash;

    private final List<Statement> statements = new ArrayList<>();

    public RootNode(SourceFile sourceFile, ScopeStash scopeStash) {
        this.sourceFile = sourceFile;
        this.scopeStash = scopeStash;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public List<Statement> childStatements() {
        return statements;
    }

    @Override
    public NodeType type() {
        return NodeType.ROOT;
    }

    @Override
    public TokenPosition position() {
        return null;
    }

    public SourceFile getSourceFile() {
        return sourceFile;
    }

    public ScopeStash getScopeStash() {
        return scopeStash;
    }
}
