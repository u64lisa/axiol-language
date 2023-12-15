package axiol.instructuion;

import java.util.List;

public class Instruction {
    private final OpCode opCode;
    private final int position;
    private final List<InstructionElement> elements;

    public Instruction(OpCode opCode, int position, List<InstructionElement> elements) {
        this.opCode = opCode;
        this.position = position;
        this.elements = elements;
    }

    public OpCode getOpCode() {
        return opCode;
    }

    public int getPosition() {
        return position;
    }

    public List<InstructionElement> getElements() {
        return elements;
    }
}
