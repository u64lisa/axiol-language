package axiol.instruction;

import axiol.instruction.value.NumberInstructionOperand;
import axiol.instruction.value.ReferenceInstructionOperand;
import axiol.instruction.value.StringInstructionOperand;
import axiol.instruction.value.UDTInstructionOperand;
import axiol.types.Type;

public abstract class InstructionOperand {

    public abstract Type size();

    public final NumberInstructionOperand asNumber() {
        return (NumberInstructionOperand) this;
    }

    public final ReferenceInstructionOperand asReference() {
        return (ReferenceInstructionOperand) this;
    }

    public final StringInstructionOperand asString() {
        return (StringInstructionOperand) this;
    }

    public final UDTInstructionOperand asUDT() {
        return (UDTInstructionOperand) this;
    }

}
