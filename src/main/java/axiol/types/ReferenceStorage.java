package axiol.types;

import axiol.parser.tree.Statement;
import axiol.parser.tree.statements.VariableStatement;
import axiol.parser.tree.statements.oop.ClassTypeStatement;
import axiol.parser.tree.statements.oop.FunctionStatement;
import axiol.parser.tree.statements.oop.StructTypeStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReferenceStorage {

    private final List<Reference> references = new ArrayList<>();

    public Optional<Reference> getReferenceToStatement(Statement statement) {
        if (statement instanceof ClassTypeStatement classTypeStatement) {
            return this.references.stream()
                    .filter(reference -> reference.getUuid().equals(classTypeStatement.getUuid())
                            && reference.getType() == ReferenceType.CLASS).findFirst();
        }
        if (statement instanceof VariableStatement variableStatement) {
            return this.references.stream()
                    .filter(reference -> reference.getUuid().equals(variableStatement.getUuid())
                            && reference.getType() == ReferenceType.VAR).findFirst();
        }
        if (statement instanceof FunctionStatement functionStatement) {
            return this.references.stream()
                    .filter(reference -> reference.getUuid().equals(functionStatement.getUuid())
                            && reference.getType() == ReferenceType.FUNCTION).findFirst();
        }
        if (statement instanceof StructTypeStatement structTypeStatement) {
            return this.references.stream()
                    .filter(reference -> reference.getUuid().equals(structTypeStatement.getUuid())
                            && reference.getType() == ReferenceType.STRUCT).findFirst();
        }

        return Optional.empty();
    }

    public void addReference(Reference reference) {
        this.references.add(reference);
    }

    public List<Reference> getReferences() {
        return references;
    }

    public void addAll(List<Reference> references) {
        getReferences().addAll(references);
    }
}
