package axiol.instruction.reference;

import axiol.parser.statement.Accessibility;
import axiol.types.Reference;
import axiol.types.ReferenceType;
import axiol.types.SimpleType;

import java.util.UUID;

public class InstructionReference extends Reference {

    private final int id;

    public InstructionReference(Reference reference, int id) {
        super(reference.getProprietorPath(), reference.getType(), reference.getName(),
                reference.getValueType(), reference.getUuid(),
                reference.getAccess());

        this.id = id;
    }

    public InstructionReference(ReferenceType type, String proprietorPath, String name, SimpleType valueType,
                                UUID uuid, int id, Accessibility... access) {
        super(proprietorPath, type, name, valueType, uuid, access);
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
