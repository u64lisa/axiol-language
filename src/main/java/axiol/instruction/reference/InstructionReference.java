package axiol.instruction.reference;

import axiol.parser.statement.Accessibility;
import axiol.types.Reference;
import axiol.types.ReferenceType;
import axiol.types.SimpleType;

import java.util.UUID;

public class InstructionReference extends Reference {

    private final int id;

    public InstructionReference(Reference reference, int id) {
        super(reference.getType(), reference.getName(),
                reference.getValueType(), reference.getUuid(),
                reference.getAccess());

        this.id = id;
    }

    public InstructionReference(ReferenceType type, String name, SimpleType valueType,
                                UUID uuid, int id, Accessibility... access) {
        super(type, name, valueType, uuid, access);
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
