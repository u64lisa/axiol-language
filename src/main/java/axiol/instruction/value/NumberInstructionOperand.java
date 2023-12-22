package axiol.instruction.value;

import axiol.instruction.InstructionOperand;
import axiol.types.Type;

public class NumberInstructionOperand extends InstructionOperand {
    private final Type type;
    private final long value;

    public NumberInstructionOperand(Type type, long value) {
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
