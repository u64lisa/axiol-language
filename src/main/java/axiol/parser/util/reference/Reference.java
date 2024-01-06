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
    private final Accessibility[] access;

    private String ident;
    private int identId;

    private boolean imported;
    private boolean exported;
    private boolean constant;

    public Reference(ReferenceType type, String name, Namespace location, Type valueType, Accessibility... access) {
        this.type = type;
        this.name = name;
        this.location = location;
        this.valueType = valueType;
        this.access = access;
    }
    public Reference(ReferenceType type, String name, Namespace location, Accessibility... access) {
        this.type = type;
        this.name = name;
        this.location = location;
        this.valueType = Type.NONE;
        this.access = access;
    }

    public int getIdentId() {
        return identId;
    }

    public void setIdentId(int identId) {
        this.identId = identId;
    }

    public void setExported(boolean exported) {
        this.exported = exported;
    }

    public void setImported(boolean imported) {
        this.imported = imported;
    }

    public void setConstant(boolean constant) {
        this.constant = constant;
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

    public Type getValueType() {
        return valueType;
    }

    public boolean isExported() {
        return exported;
    }

    public boolean isImported() {
        return imported;
    }

    public boolean isConstant() {
        return constant;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public String getIdent() {
        return ident;
    }

    @Override
    public String toString() {
        return "Reference{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", location=" + location +
                ", valueType=" + valueType +
                ", access=" + Arrays.toString(access) +
                ", imported=" + imported +
                ", exported=" + exported +
                '}';
    }

    public String getPath() {
        return getLocation().isRoot() ? name : getLocation().getPath() + "::" + name;
    }
}
