package axiol.types;

import java.util.Objects;

public class Type {

    private final String name;
    private final PrimitiveTypes primitiveTypes;

    // this depth is for builtins string -> u8[]
    private final int arrayDepth;

    public Type(String name, PrimitiveTypes primitiveTypes, int arrayDepth) {
        this.name = name;
        this.primitiveTypes = primitiveTypes;
        this.arrayDepth = arrayDepth;
    }

    public SimpleType toSimpleType() {
        return new SimpleType(this,0, 0);
    }

    public String getName() {
        return name;
    }

    public int getArrayDepth() {
        return arrayDepth;
    }

    public PrimitiveTypes getPrimitiveTypes() {
        return primitiveTypes;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Type type = (Type) object;
        return arrayDepth == type.arrayDepth && Objects.equals(name, type.name) && primitiveTypes == type.primitiveTypes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, primitiveTypes, arrayDepth);
    }

    @Override
    public String toString() {
        return "Type{" +
                "name='" + name + '\'' +
                ", primitiveTypes=" + primitiveTypes +
                ", arrayDepth=" + arrayDepth +
                '}';
    }
}
