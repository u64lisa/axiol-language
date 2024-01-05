package axiol.instruction.reference;

import axiol.parser.statement.Accessibility;
import axiol.parser.util.reference.Reference;
import axiol.parser.util.reference.ReferenceType;
import axiol.parser.scope.Namespace;
import axiol.types.Type;

import java.util.UUID;

public class InstructionReference extends Reference {

    private final int id;

    public InstructionReference(Reference reference, int id) {
        super(reference.getType(), reference.getName(), reference.getLocation(),
                reference.getValueType(), reference.getUuid(),
                reference.getAccess());

        this.id = id;
    }

    public InstructionReference(ReferenceType type, Namespace namespace, String name, Type valueType,
                                UUID uuid, int id, Accessibility... access) {
        super(type, name, namespace, valueType, uuid, access);
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
