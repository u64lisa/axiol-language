package axiol.types;

import axiol.holder.Proprietor;
import axiol.parser.statement.Accessibility;

import java.util.Arrays;
import java.util.UUID;

public class Reference implements Proprietor {

    private final String proprietorPath;

    private final ReferenceType type;
    private final String name;
    private final SimpleType valueType;
    private final UUID uuid;
    private final Accessibility[] access;

    public Reference(String proprietorPath, ReferenceType type, String name, SimpleType valueType, UUID uuid, Accessibility... access) {
        this.proprietorPath = proprietorPath;

        this.type = type;
        this.name = name;
        this.valueType = valueType;
        this.uuid = uuid;
        this.access = access;
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
    public String getProprietorPath() {
        return proprietorPath;
    }

    @Override
    public String toString() {
        return "Reference{" +
                "proprietorPath='" + proprietorPath + '\'' +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", valueType=" + valueType +
                ", uuid=" + uuid +
                ", access=" + Arrays.toString(access) +
                '}';
    }
}
