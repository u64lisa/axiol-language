package axiol.instruction;

import axiol.instruction.reference.InstructionReference;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ProgramElement {
    private final List<Instruction> list;
    private final ProgramType type;
    private InstructionReference reference;
    private List<InstructionReference> parameters;

    public ProgramElement(ProgramType type) {
        this.list = new ArrayList<>();
        this.type = type;
    }

    public ProgramType getType() {
        return type;
    }

    public InstructionReference getReference() {
        return reference;
    }

    public List<InstructionReference> getParameters() {
        return parameters;
    }

    public List<Instruction> getInstructions() {
        return list;
    }

    public ProgramElement instruction(OpCode opCode, Consumer<InstructionBuilder> builder) {
        InstructionBuilder instructionBuilder = new InstructionBuilder();
        instructionBuilder.opcode(opCode);
        builder.accept(instructionBuilder);
        this.list.add(instructionBuilder.build());

        return this;
    }

    public void setParameters(List<InstructionReference> parameters) {
        this.parameters = parameters;
    }

    public void setReference(InstructionReference reference) {
        this.reference = reference;
    }

    @Override
    public String toString() {
        return switch (type) {
            case CODE -> super.toString();
            default -> reference.getPath();
        };
    }

}