package axiol.instruction.value;

import axiol.instruction.InstructionOperand;
import axiol.types.Type;
import axiol.types.TypeCollection;

public class StringInstructionOperand extends InstructionOperand {
    private final String value;

    public StringInstructionOperand(String value) {
        this.value = value;
    }

    @Override
    public Type size() {
        return TypeCollection.STRING;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "StringInstructionOperand{" +
                "value='" + value + '\'' +
                '}';
    }
}
