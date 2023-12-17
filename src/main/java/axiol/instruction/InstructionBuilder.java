package axiol.instruction;

import java.util.ArrayList;
import java.util.List;

public class InstructionBuilder {
    private final List<InstructionElement> elements = new ArrayList<>();
    private OpCode opCode = null;
    private int position = -1;

    public InstructionBuilder addPosition(int position) {
        this.position = position;
        return this;
    }

    public InstructionBuilder addOpCode(OpCode opCode) {
        this.opCode = opCode;
        return this;
    }

    public InstructionBuilder addInstructionElement(InstructionElement element) {
        elements.add(element);

        return this;
    }

    public Instruction build() {
        assert this.opCode != null && position != -1: "invalid build process";
        return new Instruction(opCode, position, elements);
    }
}
