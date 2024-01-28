package axiol.instruction.reference;

import axiol.parser.statement.Accessibility;
import axiol.parser.util.reference.Reference;
import axiol.parser.util.reference.ReferenceType;
import axiol.parser.scope.objects.Namespace;
import axiol.types.Type;

public class InstructionReference extends Reference {

    private final int id;

    public InstructionReference(Reference reference, int id) {
        super(reference.getType(), reference.getName(), reference.getLocation(),
                reference.getValueType(),
                reference.getAccess());

        this.id = id;
    }

    public InstructionReference(ReferenceType type, Namespace namespace, String name,
                                Type valueType, int id, Accessibility... access) {
        super(type, name, namespace, valueType, access);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String toSimpleString() {
       //String type = switch (flags & 0x1f) {
       //    case VARIABLE -> "var";
       //    case LABEL -> "lab";
       //    case FUNCTION -> "fun";
       //    case NAMESPACE -> "ns";
       //    default -> "unk";
       //};
       //
       //return type + "_" + id + (isExported() ? "_export" : "") + (isImported() ? "_import" : "" + (isConst() ? "_const" : ""));
        return null; // todo implement
    }
}
