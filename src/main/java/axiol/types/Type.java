package axiol.types;

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
    public String toString() {
        return "Type{" +
                "name='" + name + '\'' +
                ", primitiveTypes=" + primitiveTypes +
                ", arrayDepth=" + arrayDepth +
                '}';
    }
}
