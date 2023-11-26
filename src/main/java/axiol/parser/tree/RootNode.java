package axiol.parser.tree;

import axiol.parser.util.SourceFile;

import java.util.ArrayList;
import java.util.List;

public class RootNode extends Statement {

    private final SourceFile sourceFile;

    private final List<Statement> statements = new ArrayList<>();

    public RootNode(SourceFile sourceFile) {
        this.sourceFile = sourceFile;
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

    public SourceFile getSourceFile() {
        return sourceFile;
    }
}
