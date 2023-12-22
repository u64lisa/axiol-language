package axiol.instruction.value;

import axiol.instruction.InstructionOperand;
import axiol.instruction.reference.InstructionReference;
import axiol.types.Type;

public class ReferenceInstructionOperand extends InstructionOperand {

    private final InstructionReference reference;

    public ReferenceInstructionOperand(InstructionReference reference) {
        this.reference = reference;
    }

    @Override
    public Type size() {
        return reference.getValueType().getType();
    }

    public InstructionReference getReference() {
        return reference;
    }
}
