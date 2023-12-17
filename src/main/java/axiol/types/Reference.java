package axiol.types;

import axiol.parser.statement.Accessibility;

public class Reference {

    private final ReferenceType type;
    private final String name;
    private final SimpleType valueType;
    private final Accessibility[] access;

    public Reference(ReferenceType type, String name, SimpleType valueType, Accessibility... access) {
        this.type = type;
        this.name = name;
        this.valueType = valueType;
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

    public SimpleType getValueType() {
        return valueType;
    }
}
