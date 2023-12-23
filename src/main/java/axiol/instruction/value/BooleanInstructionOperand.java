package axiol.instruction.value;

import axiol.instruction.InstructionOperand;
import axiol.types.Type;
import axiol.types.TypeCollection;

public class BooleanInstructionOperand extends InstructionOperand {

    private final boolean value;

    public BooleanInstructionOperand(boolean value) {
        this.value = value;
    }

    @Override
    public Type size() {
        return TypeCollection.BOOLEAN;
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
