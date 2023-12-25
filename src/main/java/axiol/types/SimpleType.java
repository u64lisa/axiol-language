package axiol.types;

import java.util.Objects;

public class SimpleType {
    private final Type type;
    private final int arrayDepth;
    private final int pointerDepth;

    public SimpleType(Type type, int arrayDepth, int pointerDepth) {
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

    @Deprecated
    public SimpleType increaseArrayDepth(int length) {
        return new SimpleType(this.type,arrayDepth + length, pointerDepth);
    }

    @Deprecated
    public SimpleType increasePointerDepth(int length) {
        return new SimpleType(this.type,arrayDepth, pointerDepth + length);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        SimpleType type1 = (SimpleType) object;
        return arrayDepth == type1.arrayDepth && pointerDepth == type1.pointerDepth && Objects.equals(type, type1.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, arrayDepth, pointerDepth);
    }

    @Override
    public String toString() {
        return "ParsedType{" +
                "type=" + type +
                ", arrayDepth=" + arrayDepth +
                ", pointerDepth=" + pointerDepth +
                '}';
    }

    public boolean assetEqualityFor(SimpleType rightType) {
        if (rightType.type.equals(this.type)) {
            return this.arrayDepth == rightType.arrayDepth &&
                    this.pointerDepth == rightType.pointerDepth;
        }
        return false;
    }
}
