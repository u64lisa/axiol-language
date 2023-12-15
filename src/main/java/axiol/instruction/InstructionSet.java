package axiol.instruction;

import java.util.List;

public class InstructionSet {

    private final List<Instruction> instructions;

    public InstructionSet(List<Instruction> instructions) {
        this.instructions = instructions;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }
}
