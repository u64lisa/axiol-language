package axiol.parser.util.reference;

import axiol.parser.statement.Accessibility;
import axiol.parser.scope.Namespace;
import axiol.types.Type;

import java.util.Arrays;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reference reference = (Reference) o;
        return imported == reference.imported && exported == reference.exported && constant ==
                reference.constant && type == reference.type && Objects.equals(name, reference.name)
                && Objects.equals(location, reference.location) && Objects.equals(valueType, reference.valueType)
                && Arrays.equals(access, reference.access) && Objects.equals(ident, reference.ident);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(type, name, location, valueType, ident, imported, exported, constant);
        result = 31 * result + Arrays.hashCode(access);
        return result;
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
                ", ident='" + ident + '\'' +
                ", identId=" + identId +
                ", imported=" + imported +
                ", exported=" + exported +
                ", constant=" + constant +
                '}';
    }

    public String getPath() {
        return getLocation().isRoot() ? name : getLocation().getPath() + "::" + name;
    }
}
