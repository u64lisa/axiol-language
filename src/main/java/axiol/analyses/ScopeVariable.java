package axiol.analyses;

import axiol.types.SimpleType;

public class ScopeVariable {

    private final String name;
    private final SimpleType type;

    public ScopeVariable(String name, SimpleType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public SimpleType getType() {
        return type;
    }
}
