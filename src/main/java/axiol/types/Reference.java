package axiol.types;

import axiol.holder.Proprietor;
import axiol.parser.statement.Accessibility;

import java.util.Arrays;
import java.util.UUID;

public class Reference {

    private final ReferenceType type;
    private final String name;
    private SimpleType valueType;
    private final UUID uuid;
    private final Accessibility[] access;

    public Reference(ReferenceType type, String name, SimpleType valueType, UUID uuid, Accessibility... access) {
        this.type = type;
        this.name = name;
        this.valueType = valueType;
        this.uuid = uuid;
        this.access = access;
    }

    public void setValueType(SimpleType valueType) {
        this.valueType = valueType;
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

    public SimpleType getValueType() {
        return valueType;
    }



    @Override
    public String toString() {
        return "Reference{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", valueType=" + valueType +
                ", uuid=" + uuid +
                ", access=" + Arrays.toString(access) +
                '}';
    }
}
