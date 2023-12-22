package axiol.instruction;

import java.util.function.Consumer;

public class InstructionSetBuilder {

    private final InstructionSet instructionSet;

    public InstructionSetBuilder() {
        instructionSet = new InstructionSet();
    }

    public InstructionSetBuilder instruction(OpCode opCode, Consumer<InstructionBuilder> builder) {
        InstructionBuilder instructionBuilder = new InstructionBuilder();
        instructionBuilder.opcode(opCode);
        builder.accept(instructionBuilder);
        this.instructionSet.getInstructions().add(instructionBuilder.build());
        return this;
    }

    public InstructionSetBuilder instruction(Consumer<InstructionBuilder> builder) {
        InstructionBuilder instructionBuilder = new InstructionBuilder();
        builder.accept(instructionBuilder);
        this.instructionSet.getInstructions().add(instructionBuilder.build());
        return this;
    }

    public InstructionSet build() {
        return instructionSet;
    }
}
