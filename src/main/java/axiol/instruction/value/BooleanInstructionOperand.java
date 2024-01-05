package axiol.instruction.value;

import axiol.instruction.InstructionOperand;
import axiol.types.Type;

public class BooleanInstructionOperand extends InstructionOperand {

    private final boolean value;

    public BooleanInstructionOperand(boolean value) {
        this.value = value;
    }

    @Override
    public Type size() {
        return Type.BOOLEAN;
    }

    public boolean isValue() {
        return value;
    }

    @Override
    public String toString() {
        return "BooleanInstructionOperand{" +
                "value=" + value +
                '}';
    }
}
