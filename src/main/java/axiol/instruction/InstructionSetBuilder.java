package axiol.instruction;

import axiol.instruction.reference.InstructionReference;
import axiol.types.*;

import java.util.UUID;
import java.util.function.Consumer;

public class InstructionSetBuilder {

    private final InstructionSet instructionSet;

    public InstructionSetBuilder() {
        instructionSet = new InstructionSet();
    }


    public InstructionReference createDataReference(String name, SimpleType simpleType, int referenceId) {
        return new InstructionReference(new Reference("I", ReferenceType.VAR, name, simpleType, UUID.randomUUID()), referenceId++);
    }

    public InstructionReference createStringReference(int referenceId) {
        return new InstructionReference(new Reference("I",ReferenceType.VAR, ".str", TypeCollection.STRING.toSimpleType(), UUID.randomUUID()), referenceId++);
    }

    public InstructionReference createNumberReference(Type type, int referenceId) {
        return new InstructionReference(new Reference("I",ReferenceType.VAR, ".num", type.toSimpleType(), UUID.randomUUID()), referenceId++);
    }

    public InstructionReference createBooleanReference(int referenceId) {
        return new InstructionReference(new Reference("I",ReferenceType.VAR, ".bool", TypeCollection.BOOLEAN.toSimpleType(), UUID.randomUUID()), referenceId++);
    }

    public InstructionReference createNoneReference(int referenceId) {
        return new InstructionReference(new Reference("I",ReferenceType.VAR, ".none", TypeCollection.NONE.toSimpleType(), UUID.randomUUID()), referenceId++);
    }

    public InstructionReference createLocalReference(String name, int referenceId) {
        return new InstructionReference(new Reference("I",ReferenceType.VAR, name, TypeCollection.NONE.toSimpleType(), UUID.randomUUID()), referenceId++);
    }

    public InstructionReference createLabel(String name, int referenceId) {
        return createLocalReference(name, referenceId);
    }

    public InstructionSetBuilder instruction(OpCode opCode, Consumer<InstructionBuilder> builder) {
        InstructionBuilder instructionBuilder = new InstructionBuilder();
        instructionBuilder.opcode(opCode);
        builder.accept(instructionBuilder);
        this.instructionSet.getInstructions().add(instructionBuilder.build());
        return this;
    }

    public InstructionSetBuilder instructionWithoutOpCode(Consumer<InstructionBuilder> builder) {
        InstructionBuilder instructionBuilder = new InstructionBuilder();
        builder.accept(instructionBuilder);
        this.instructionSet.getInstructions().add(instructionBuilder.build());
        return this;
    }

    public InstructionSet build() {
        return instructionSet;
    }


}
