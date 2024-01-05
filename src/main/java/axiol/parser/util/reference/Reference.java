package axiol.parser.util.reference;

import axiol.parser.statement.Accessibility;
import axiol.parser.scope.Namespace;
import axiol.types.Type;

import java.util.Arrays;
import java.util.UUID;

public class Reference {

    private final ReferenceType type;
    private final String name;
    private final Namespace location;
    private Type valueType;
    private final UUID uuid;
    private final Accessibility[] access;

    private boolean imported;
    private boolean exported;

    public Reference(ReferenceType type, String name, Namespace location, Type valueType, UUID uuid, Accessibility... access) {
        this.type = type;
        this.name = name;
        this.location = location;
        this.valueType = valueType;
        this.uuid = uuid;
        this.access = access;
    }
    public Reference(ReferenceType type, String name, Namespace location, Type valueType, Accessibility... access) {
        this.type = type;
        this.name = name;
        this.location = location;
        this.valueType = valueType;
        this.uuid = UUID.randomUUID();
        this.access = access;
    }
    public Reference(ReferenceType type, String name, Namespace location, Accessibility... access) {
        this.type = type;
        this.name = name;
        this.location = location;
        this.valueType = Type.NONE;
        this.uuid = UUID.randomUUID();
        this.access = access;
    }

    public void setExported(boolean exported) {
        this.exported = exported;
    }

    public void setImported(boolean imported) {
        this.imported = imported;
    }

    public void setValueType(Type valueType) {
        this.valueType = valueType;
    }

    public Namespace getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public Accessibility[] getAccess() {
        return access;
    }

    public ReferenceType getType() {
        return type;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Type getValueType() {
        return valueType;
    }

    public boolean isExported() {
        return exported;
    }

    public boolean isImported() {
        return imported;
    }

    @Override
    public String toString() {
        return "Reference{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", location=" + location +
                ", valueType=" + valueType +
                ", uuid=" + uuid +
                ", access=" + Arrays.toString(access) +
                ", imported=" + imported +
                ", exported=" + exported +
                '}';
    }
}
