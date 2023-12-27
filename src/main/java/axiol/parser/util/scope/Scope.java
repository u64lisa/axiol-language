package axiol.parser.util.scope;

import axiol.types.Reference;

import java.util.List;

public class Scope implements ScopeAble {

    private final List<ScopeAble> containingScopes;

    private final String name;
    private final ScopeElementType type;
    private final Reference reference;

    public Scope(List<ScopeAble> containingScopes, String name, ScopeElementType type, Reference reference) {
        this.containingScopes = containingScopes;
        this.name = name;
        this.type = type;
        this.reference = reference;
    }

    public Reference getReference() {
        return reference;
    }

    public ScopeElementType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public List<ScopeAble> getContainingScopes() {
        return containingScopes;
    }

}
