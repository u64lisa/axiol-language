package axiol.instruction;

import axiol.instruction.reference.InstructionReference;
import axiol.instruction.value.NumberInstructionOperand;
import axiol.instruction.value.ReferenceInstructionOperand;
import axiol.instruction.value.StringInstructionOperand;
import axiol.types.PrimitiveTypes;
import axiol.types.Reference;

import java.util.ArrayList;
import java.util.List;

public class InstructionBuilder {
    private final List<InstructionOperand> elements = new ArrayList<>();
    private OpCode opCode = null;
    private int position = -1;

    public InstructionBuilder addPosition(int position) {
        this.position = position;
        return this;
    }

    public InstructionBuilder opcode(OpCode opCode) {
        this.opCode = opCode;
        return this;
    }

    public InstructionBuilder operand(InstructionOperand element) {
        elements.add(element);
        return this;
    }

    public InstructionBuilder numberOperand(PrimitiveTypes type, long value) {
        elements.add(new NumberInstructionOperand(type.toType(), value));
        return this;
    }

    public InstructionBuilder stringOperand(String text) {
        elements.add(new StringInstructionOperand(text));
        return this;
    }

    public InstructionBuilder referenceOperand(Reference reference, int referenceId) {
        elements.add(new ReferenceInstructionOperand(new InstructionReference(reference, referenceId++)));
        return this;
    }

    public Instruction build() {
        assert this.opCode != null && position != -1: "invalid build process";
        return new Instruction(opCode, position, elements);
    }
}
