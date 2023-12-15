package axiol.instructuion.reference;

import axiol.types.Type;

public class InstructionReference {

    private final int id;
    private final String name;
    private final Type type;

    public InstructionReference(int id, String name, Type type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

}
