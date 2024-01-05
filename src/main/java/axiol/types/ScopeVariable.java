package axiol.types;

public class ScopeVariable {

    private final String name;
    private final Type type;

    public ScopeVariable(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ScopeVariable{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
