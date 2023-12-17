package axiol.parser.tree;

import axiol.parser.tree.statements.VariableStatement;
import axiol.parser.tree.statements.oop.ClassTypeStatement;
import axiol.parser.tree.statements.oop.FunctionStatement;
import axiol.parser.util.SourceFile;
import axiol.parser.util.error.TokenPosition;
import axiol.types.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RootNode extends Statement {

    private final SourceFile sourceFile;

    private final List<Reference> references = new ArrayList<>();
    private final List<Statement> statements = new ArrayList<>();

    public RootNode(SourceFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public List<Reference> getReferences() {
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

    public Optional<Reference> getReferenceToStatement(Statement statement) {
        if (statement instanceof ClassTypeStatement classTypeStatement) {
            return this.references.stream()
                    .filter(reference -> reference.getUuid().equals(classTypeStatement.getUuid())).findFirst();
        }
        if (statement instanceof VariableStatement variableStatement) {
            return this.references.stream()
                    .filter(reference -> reference.getUuid().equals(variableStatement.getUuid())).findFirst();
        }
        if (statement instanceof FunctionStatement functionStatement) {
            return this.references.stream()
                    .filter(reference -> reference.getUuid().equals(functionStatement.getUuid())).findFirst();
        }

        return Optional.empty();
    }

    public SourceFile getSourceFile() {
        return sourceFile;
    }
}
