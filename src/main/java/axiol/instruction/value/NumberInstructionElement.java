package axiol.instruction.value;

import axiol.instruction.InstructionElement;
import axiol.types.Type;

public class NumberInstructionElement extends InstructionElement {
    private final Type type;
    private final long value;

    public NumberInstructionElement(Type type, long value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public Type size() {
        return type;
    }

    public long getValue() {
        return value;
    }
}