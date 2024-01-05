package axiol.parser.statement;

import axiol.parser.tree.Expression;
import axiol.parser.util.reference.Reference;
import axiol.types.Type;

public final class Parameter {
    private final String name;
    private final Type type;
    private final Expression defaultValue;
    private final boolean pointer;
    private final boolean referenced;
    private final Reference reference;

    public Parameter(String name, Type type, Expression defaultValue, boolean pointer, boolean referenced, Reference reference) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.pointer = pointer;
        this.referenced = referenced;
        this.reference = reference;
    }

    public String getName() {
        return name;
    }

    public Expression getDefaultValue() {
        return defaultValue;
    }

    public Type getParsedType() {
        return type;
    }

    public boolean isPointer() {
        return pointer;
    }

    public boolean isReferenced() {
        return referenced;
    }

    public Reference getReference() {
        return reference;
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "name='" + name + '\'' +
                ", parsedType=" + type +
                ", defaultValue=" + defaultValue +
                ", pointer=" + pointer +
                ", referenced=" + referenced +
                '}';
    }
}