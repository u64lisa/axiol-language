package axiol.instruction;

import axiol.instruction.reference.InstructionReference;
import axiol.parser.util.reference.Reference;
import axiol.parser.util.reference.ReferenceType;
import axiol.parser.scope.objects.Namespace;
import axiol.types.*;

import java.util.function.Consumer;

public class InstructionSetBuilder {

    private final Namespace NONE = new Namespace();

    private final InstructionSet instructionSet;

    public InstructionSetBuilder() {
        instructionSet = new InstructionSet();
    }


    public InstructionReference createDataReference(String name, Type type, int referenceId) {
        return new InstructionReference(new Reference(ReferenceType.VAR, name, NONE, type), referenceId++);
    }

    public InstructionReference createStringReference(int referenceId) {
        return new InstructionReference(new Reference(ReferenceType.VAR, ".str", NONE, Type.STRING), referenceId++);
    }

    public InstructionReference createNumberReference(Type type, int referenceId) {
        return new InstructionReference(new Reference(ReferenceType.VAR, ".num", NONE, type), referenceId++);
    }

    public InstructionReference createBooleanReference(int referenceId) {
        return new InstructionReference(new Reference(ReferenceType.VAR, ".bool", NONE, Type.BOOLEAN), referenceId++);
    }

    public InstructionReference createNoneReference(int referenceId) {
        return new InstructionReference(new Reference(ReferenceType.VAR, ".none", NONE, Type.NONE), referenceId++);
    }

    public InstructionReference createLocalReference(String name, int referenceId) {
        return new InstructionReference(new Reference(ReferenceType.VAR, name, NONE, Type.NONE), referenceId++);
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
