package axiol.instruction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InstructionSet {

    private final List<Instruction> instructions = new ArrayList<>();

    public InstructionSet() {
    }

    public void instruction(Consumer<InstructionBuilder> instructionBuilderConsumer) {
        InstructionBuilder instructionBuilder = new InstructionBuilder();
        instructionBuilderConsumer.accept(instructionBuilder);
        this.instructions.add(instructionBuilder.build());
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }
}
