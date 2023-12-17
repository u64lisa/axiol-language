package axiol.instruction;

import axiol.instruction.value.NumberInstructionElement;
import axiol.instruction.value.ReferenceInstructionElement;
import axiol.instruction.value.StringInstructionElement;
import axiol.instruction.value.UDTInstructionElement;
import axiol.types.Type;

public abstract class InstructionElement {

    public abstract Type size();

    public final NumberInstructionElement asNumber() {
        return (NumberInstructionElement) this;
    }

    public final ReferenceInstructionElement asReference() {
        return (ReferenceInstructionElement) this;
    }

    public final StringInstructionElement asString() {
        return (StringInstructionElement) this;
    }

    public final UDTInstructionElement asUDT() {
        return (UDTInstructionElement) this;
    }

}
