package axiol.types;

public class ParsedType {
    private final Type type;
    private final int arrayDepth;

    public ParsedType(Type type, int arrayDepth) {
        this.type = type;
        this.arrayDepth = arrayDepth;
    }

    public int getArrayDepth() {
        return arrayDepth;
    }

    public Type getType() {
        return type;
    }
}
