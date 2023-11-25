package axiol.parser.statement;

import axiol.parser.tree.Expression;
import axiol.types.ParsedType;

public final class Parameter {
    private final String name;
    private final ParsedType parsedType;
    private final Expression defaultValue;
    private final boolean pointer;
    private final boolean referenced;

    public Parameter(String name, ParsedType parsedType, Expression defaultValue, boolean pointer, boolean referenced) {
        this.name = name;
        this.parsedType = parsedType;
        this.defaultValue = defaultValue;
        this.pointer = pointer;
        this.referenced = referenced;
    }

    public String getName() {
        return name;
    }

    public Expression getDefaultValue() {
        return defaultValue;
    }

    public ParsedType getParsedType() {
        return parsedType;
    }

    public boolean isPointer() {
        return pointer;
    }

    public boolean isReferenced() {
        return referenced;
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "name='" + name + '\'' +
                ", parsedType=" + parsedType +
                ", defaultValue=" + defaultValue +
                ", pointer=" + pointer +
                ", referenced=" + referenced +
                '}';
    }
}