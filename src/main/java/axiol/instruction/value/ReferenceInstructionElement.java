package axiol.instruction.value;

import axiol.instruction.InstructionElement;
import axiol.instruction.reference.InstructionReference;
import axiol.types.Type;

public class ReferenceInstructionElement extends InstructionElement {

    private final InstructionReference reference;

    public ReferenceInstructionElement(InstructionReference reference) {
        this.reference = reference;
    }

    @Override
    public Type size() {
        return reference.getType();
    }

    public InstructionReference getReference() {
        return reference;
    }
}
