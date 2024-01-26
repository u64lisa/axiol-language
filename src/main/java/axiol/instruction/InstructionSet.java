package axiol.instruction;

import java.util.ArrayList;
import java.util.List;

public class InstructionSet {

    private final List<ProgramElement> instructions = new ArrayList<>();

    public List<ProgramElement> getInstructions() {
        return instructions;
    }

    @Override
    public String toString() {
        return "InstructionSet{" +
                "instructions=" + instructions +
                '}';
    }
}
