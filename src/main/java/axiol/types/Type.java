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

    public boolean equals(Type other) {
        return Objects.equals(other.name, this.name) &&
                other.arrayDepth == this.arrayDepth &&
                other.primitiveTypes == this.primitiveTypes;
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
