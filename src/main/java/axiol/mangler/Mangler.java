package axiol.mangler;

import axiol.parser.scope.objects.Namespace;
import axiol.parser.statement.Accessibility;
import axiol.parser.util.reference.Reference;
import axiol.types.Type;

import javax.print.DocFlavor;
import java.util.Arrays;
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

    public static String mangleClass(String path, Accessibility... access) {
        return new StringBuilder().append(path).append(SEPARATION_CHAR).append(mangleAccess(access)).toString();
    }

    public static String mangleAccess(Accessibility... access) {
        StringBuilder builder = new StringBuilder();
        for (Accessibility accessibility : access) {
            builder.append(BASE64.charAt(accessibility.ordinal())).append(SEPARATION_CHAR);
        }
        return builder.toString();
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