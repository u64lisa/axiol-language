package axiol.parser.util.scope;

import axiol.types.Reference;

import javax.xml.crypto.URIDereferencer;
import java.util.List;
import java.util.UUID;

public class Scope implements ScopeAble {

    private final List<ScopeAble> containingScopes;

    private final Scope parentScope;

    private final UUID uuid;

    private final String name;
    private final ScopeElementType type;
    private final Reference reference;

    public Scope(List<ScopeAble> containingScopes, Scope parentScope, String name, ScopeElementType type, Reference reference) {
        this.containingScopes = containingScopes;
        this.parentScope = parentScope;
        this.name = name;
        this.type = type;
        this.reference = reference;

        this.uuid = UUID.randomUUID();
    }

    public static Reference findReference(Scope currentScope, String name) {
        if (currentScope == null)
            return null;

        Reference reference = null;
        for (ScopeAble containingScope : currentScope.containingScopes) {
            if (containingScope instanceof ScopeElement scopeElement) {
                if (scopeElement.getName().equals(name))
                    reference = scopeElement.getReference();
            }
        }

        return reference == null ? findReference(currentScope.parentScope, name) : reference;
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

    public UUID getUuid() {
        return uuid;
    }

    public List<ScopeAble> getContainingScopes() {
        return containingScopes;
    }

    public Scope getParentScope() {
        return parentScope;
    }

    @Override
    public String toString() {
        return "Scope{" +
                "containingScopes=" + containingScopes +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", reference=" + reference +
                '}';
    }
}
