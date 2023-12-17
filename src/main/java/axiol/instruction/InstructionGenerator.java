package axiol.instruction;

import axiol.parser.tree.RootNode;

public class InstructionGenerator {
    private final InstructionSet instructionSet = new InstructionSet();

    public InstructionGenerator() {
        instructionSet.getInstructions().clear();
    }

    public InstructionSet generate(RootNode rootNode) {
        // todo process tree

        return instructionSet;
    }
}
