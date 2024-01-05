package axiol.types;

public class Type {

    public static final Type STRING =  new Type("str", 1, 0,8,false,false,true);
    public static final Type CHAR =    new Type("u8",     0, 0, 8,false, false, true);
    public static final Type BOOLEAN = new Type("u8",     0, 0, 8,false, false, true);

    public static final Type NONE =    new Type("_none_", -1, -1, -1, false, false, false);
    public static final Type VOID =    new Type("void",  0, 0, 0, false, false, false);

    public static final Type I8 =      new Type("i8",     0, 0, 8,false, false, false);
    public static final Type I16 =     new Type("i16",    0, 0, 16,false, false, false);
    public static final Type I32 =     new Type("i32",    0, 0, 32,false, false, false);
    public static final Type I64 =     new Type("i64",    0, 0, 64,false, false, false);

    public static final Type U8 =      new Type("u8",     0, 0, 8,false, false, true);
    public static final Type U16 =     new Type("u16",    0, 0, 16,false, false, true);
    public static final Type U32 =     new Type("u32",    0, 0, 32,false, false, true);
    public static final Type U64 =     new Type("u64",    0, 0, 64,false, false, true);

    public static final Type I128 =    new Type("i128",  0, 0, 128,false, false, false);
    public static final Type U128 =    new Type("u128",  0, 0, 128,false, false, true);

    public static final Type F32 =     new Type("f32",    0, 0, 32,false, true, false);
    public static final Type F64 =     new Type("f64",    0, 0, 64,false, true, false);

    public static final Type U0 =      new Type("u0",     0, 0, 0,false, false, true);

    public static final Type[] ALL = new Type[] {
            I8, I16, I32, I64,
            U8, U16, U32, U64,

            F32, F64,

            I128, U128,

            STRING, CHAR, BOOLEAN,

            NONE, VOID, U0,
    };

    private final String name;
    private final int arrayDepth;
    private final int pointerDepth;

    private final int bitSize;

    private final boolean big;
    private final boolean floating;
    private final boolean unsigned;

    public Type(String name, int arrayDepth, int pointerDepth, int bitSize,
                boolean big, boolean floating, boolean unsigned) {

        this.name = name;
        this.arrayDepth = arrayDepth;
        this.pointerDepth = pointerDepth;
        this.bitSize = bitSize;
        this.big = big;
        this.floating = floating;
        this.unsigned = unsigned;
    }

    public Type increasePointerDepth(int size) {
        return new Type(name, arrayDepth,
                pointerDepth + size, bitSize, big, floating, unsigned);
    }
    public Type increaseArrayDepth(int size) {
        return new Type(name, arrayDepth + size,
                pointerDepth, bitSize, big, floating, unsigned);
    }

    public int getArrayDepth() {
        return arrayDepth;
    }

    public int getPointerDepth() {
        return pointerDepth;
    }

    public String getName() {
        return name;
    }

    public boolean isBig() {
        return big;
    }

    public boolean isFloating() {
        return floating;
    }

    public boolean isUnsigned() {
        return unsigned;
    }
}
