package axiol.types;

import axiol.lexer.Token;

import java.util.Arrays;
import java.util.List;

public class TypeCollection {

    public static final SimpleType STRING = new Type("str", PrimitiveTypes.U8, 1).toSimpleType();
    public static final SimpleType CHAR = new Type("char", PrimitiveTypes.U8, 0).toSimpleType();
    public static final SimpleType BOOLEAN = new Type("boolean", PrimitiveTypes.I8, 0).toSimpleType();

    public static final SimpleType NONE = new Type("_none_", PrimitiveTypes.U0, -1).toSimpleType();
    public static final SimpleType VOID = new Type("void", PrimitiveTypes.U0, 0).toSimpleType();

    public static final SimpleType I8 = PrimitiveTypes. U8.toType().toSimpleType();
    public static final SimpleType I16 = PrimitiveTypes.U16.toType().toSimpleType();
    public static final SimpleType I32 = PrimitiveTypes.U32.toType().toSimpleType();
    public static final SimpleType I64 = PrimitiveTypes.U64.toType().toSimpleType();

    public static final SimpleType U8 = PrimitiveTypes. U8.toType().toSimpleType();
    public static final SimpleType U16 = PrimitiveTypes.U16.toType().toSimpleType();
    public static final SimpleType U32 = PrimitiveTypes.U32.toType().toSimpleType();
    public static final SimpleType U64 = PrimitiveTypes.U64.toType().toSimpleType();

    public static final SimpleType I128 = PrimitiveTypes.I128.toType().toSimpleType();
    public static final SimpleType U128 = PrimitiveTypes.U128.toType().toSimpleType();

    public static final SimpleType F32 = PrimitiveTypes.F32.toType().toSimpleType();
    public static final SimpleType F64 = PrimitiveTypes.F64.toType().toSimpleType();

    public static final SimpleType U0 = PrimitiveTypes.U0.toType().toSimpleType();

    public static final SimpleType[] TYPES = {
            I8,
            I16,
            I32,
            I64,

            U8,
            U16,
            U32,
            U64,

            I128,
            U128,

            F32,
            F64,

            U0,

            STRING, CHAR, BOOLEAN, VOID
    };

    public static final List<SimpleType> TYPE_LIST = Arrays.stream(TYPES).toList();

    public static SimpleType typeByToken(Token token) {
        return TYPE_LIST.stream().filter(type -> type.getType().getName().equalsIgnoreCase(token.getValue())).findFirst().orElse(NONE);
    }

}
