package axiol.types;

import java.util.Arrays;
import java.util.List;

public class TypeCollection {

    public static final Type STRING = new Type("str", PrimitiveTypes.U8, 1);
    public static final Type CHAR = new Type("char", PrimitiveTypes.U8, 0);
    public static final Type BOOLEAN = new Type("boolean", PrimitiveTypes.I8, 0);

    public static final Type[] TYPES = {
            PrimitiveTypes.I8.toType(),
            PrimitiveTypes.I16.toType(),
            PrimitiveTypes.I32.toType(),
            PrimitiveTypes.I64.toType(),

            PrimitiveTypes.U8.toType(),
            PrimitiveTypes.U16.toType(),
            PrimitiveTypes.U32.toType(),
            PrimitiveTypes.U64.toType(),

            PrimitiveTypes.F32.toType(),
            PrimitiveTypes.F64.toType(),

            PrimitiveTypes.U0.toType(),

            STRING, CHAR, BOOLEAN,
    };

    public static final List<Type> TYPE_LIST = Arrays.stream(TYPES).toList();

}
