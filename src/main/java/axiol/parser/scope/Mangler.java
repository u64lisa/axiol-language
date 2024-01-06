package axiol.parser.scope;

import axiol.parser.util.reference.Reference;
import axiol.parser.util.reference.ReferenceType;
import axiol.types.Type;

import java.util.List;


public class Mangler {
    private static final String BASE64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private static final String HEX = "0123456789abcdef";

    private static final char SEPARATION_CHAR = '|';

    public static String mangleFunction(Type returnType, Namespace namespace, String name, List<Reference> parameters) {
        StringBuilder stringBuilder = new StringBuilder("%s|%s|%s".formatted(namespace.getPath(), name, mangleType(returnType)));
        for (Reference param : parameters) {
            stringBuilder.append(SEPARATION_CHAR).append(mangleType(param.getValueType()));
        }
        return stringBuilder.toString();
    }

    public static String mangleVariable(Namespace namespace, String name) {
        return "%s|%s".formatted(namespace.getPath(), name);
    }

    public static String mangleType(Type type) {
        if (type == Type.MERGED) {
            return "?";
        }
        StringBuilder sb = new StringBuilder();
        if (type.isBig())
            sb.append('b');
        else if (type.isUnsigned())
            sb.append('u');
        else if (type.isFloating())
            sb.append('f');
        else
            sb.append('s');

        sb.append('x').append(HEX.charAt(type.getArrayDepth()))
                .append('p').append(HEX.charAt(type.getPointerDepth()))

                .append(BASE64.charAt(type.getBits() % 64))
                .append(BASE64.charAt(type.getBits() / 64));
        return sb.toString();
    }

}