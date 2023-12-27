package axiol.parser.util.scope;

import axiol.types.Reference;

public class ScopeElement implements ScopeAble {

    private final String name;
    private final ScopeElementType type;
    private final Reference reference;

    public ScopeElement(String name, ScopeElementType type, Reference reference) {
        this.name = name;
        this.type = type;
        this.reference = reference;
    }

    public ScopeElementType getType() {
        return type;
    }

    public Reference getReference() {
        return reference;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ScopeElement{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", reference=" + reference +
                '}';
    }
}
