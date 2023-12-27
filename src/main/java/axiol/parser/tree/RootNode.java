package axiol.parser.tree;

import axiol.parser.tree.statements.VariableStatement;
import axiol.parser.tree.statements.oop.ClassTypeStatement;
import axiol.parser.tree.statements.oop.FunctionStatement;
import axiol.parser.tree.statements.oop.StructTypeStatement;
import axiol.parser.util.SourceFile;
import axiol.parser.util.error.TokenPosition;
import axiol.parser.util.scope.Scope;
import axiol.types.Reference;
import axiol.types.ReferenceStorage;
import axiol.types.ReferenceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RootNode extends Statement {

    private final ReferenceStorage references = new ReferenceStorage();
    private final Scope scope;
    private final SourceFile sourceFile;

    private final List<Statement> statements = new ArrayList<>();

    public RootNode(Scope scope, SourceFile sourceFile) {
        this.scope = scope;
        this.sourceFile = sourceFile;
    }

    public Scope getScope() {
        return scope;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public ReferenceStorage getReferences() {
        return references;
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
}
