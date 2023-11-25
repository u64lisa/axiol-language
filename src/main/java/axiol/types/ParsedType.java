package axiol.types;

public class ParsedType {
    private final Type type;
    private final int arrayDepth;
    private final int pointerDepth;

    public ParsedType(Type type, int arrayDepth, int pointerDepth) {
        this.type = type;
        this.arrayDepth = arrayDepth;
        this.pointerDepth = pointerDepth;
    }

    public int getArrayDepth() {
        return arrayDepth;
    }

    public Type getType() {
        return type;
    }

    public int getPointerDepth() {
        return pointerDepth;
    }

    @Override
    public String toString() {
        return "ParsedType{" +
                "type=" + type +
                ", arrayDepth=" + arrayDepth +
                ", pointerDepth=" + pointerDepth +
                '}';
    }
}
