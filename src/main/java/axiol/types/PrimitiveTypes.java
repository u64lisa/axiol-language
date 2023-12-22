package axiol.types;

import axiol.lexer.Token;

public enum PrimitiveTypes {

    U8(8, false, false),
    U16(16, false, false),
    U32(32, false, false),
    U64(64, false, false),
    U128(128, false, false),

    I8(8, true, false),
    I16(16, true, false),
    I32(32, true, false),
    I64(64, true, false),
    I128(128, true, false),

    F32(32, true, true),
    F64(64, true, true),

    U0(0, false, false),

    ;

    private final int bits;
    private final boolean signed;
    private final boolean floating;

    PrimitiveTypes(int bits, boolean signed, boolean floating) {
        this.bits = bits;
        this.signed = signed;
        this.floating = floating;
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
}
