package axiol.instruction;

import java.util.List;

public class Instruction {
    private final OpCode opCode;
    private final int position;
    private final List<InstructionOperand> elements;

    public Instruction(OpCode opCode, int position, List<InstructionOperand> elements) {
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

    public List<InstructionOperand> getElements() {
        return elements;
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "opCode=" + opCode +
                ", position=" + position +
                ", elements=" + elements +
                '}';
    }
}
