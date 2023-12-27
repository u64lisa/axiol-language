package axiol.parser.util.scope;

import axiol.types.Reference;

import java.util.List;
import java.util.UUID;

public class Scope implements ScopeAble {

    private final List<ScopeAble> containingScopes;

    private final UUID uuid;

    private final String name;
    private final ScopeElementType type;
    private final Reference reference;

    public Scope(List<ScopeAble> containingScopes, String name, ScopeElementType type, Reference reference) {
        this.containingScopes = containingScopes;
        this.name = name;
        this.type = type;
        this.reference = reference;

        this.uuid = UUID.randomUUID();
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
