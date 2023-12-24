package axiol.types;

import axiol.lexer.Token;

public enum PrimitiveTypes {

    U8(8, false, false, false),
    U16(16, false, false, false),
    U32(32, false, false, false),
    U64(64, false, false, false),
    U128(128, false, false, true),

    I8(8, true, false, false),
    I16(16, true, false, false),
    I32(32, true, false, false),
    I64(64, true, false, false),
    I128(128, true, false, true),

    F32(32, true, true, false),
    F64(64, true, true, false),

    U0(0, false, false, false),

    ;

    private final int bits;
    private final boolean signed;
    private final boolean floating;
    private final boolean big;

    PrimitiveTypes(int bits, boolean signed, boolean floating, boolean big) {
        this.bits = bits;
        this.signed = signed;
        this.floating = floating;
        this.big = big;
    }

    public static PrimitiveTypes fromToken(Token token) {
        for (PrimitiveTypes value : PrimitiveTypes.values()) {
            if (value.name().equalsIgnoreCase(token.getValue()))
                return value;
        }
        return null;
    }

    public Type toType() {
        return new Type(this.name().toLowerCase(), this, 0);
    }

    public int getBits() {
        return bits;
    }

    public boolean isSigned() {
        return signed;
    }

    public boolean isFloating() {
        return floating;
    }

    public boolean isBig() {
        return big;
    }
}
