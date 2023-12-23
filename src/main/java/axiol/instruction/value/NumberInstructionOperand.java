package axiol.instruction.value;

import axiol.instruction.InstructionOperand;
import axiol.types.Type;

public class NumberInstructionOperand extends InstructionOperand {
    private final Type type;
    private final Number value;

    public NumberInstructionOperand(Type type, Number value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public Type size() {
        return type;
    }

    public Number getValue() {
        return value;
    }
}
