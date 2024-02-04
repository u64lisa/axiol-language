package axiol.target.assembly.arm;

import axiol.instruction.Instruction;
import axiol.instruction.InstructionOperand;
import axiol.instruction.OpCode;
import axiol.instruction.ProgramElement;
import axiol.instruction.reference.InstructionReference;
import axiol.instruction.value.ReferenceInstructionOperand;
import axiol.parser.util.reference.ReferenceType;
import axiol.target.assembly.AssemblyProgramElement;
import axiol.target.assembly.AssemblyTranslation;

import java.util.HashSet;
import java.util.Set;

public class ARMAssemblyProgramElement extends AssemblyProgramElement {

    public ARMAssemblyProgramElement(AssemblyTranslation translation, ProgramElement procedure) {
        super(procedure);

        Set<InstructionReference> seenVariables = new HashSet<>();

        for (InstructionReference ref : params) {
            int size = translation.getTypeByteSize(ref.getValueType());
            stackSize += size; // ((size + 7) & ~7);
            stackOffset.put(ref, stackSize);
            seenVariables.add(ref);
        }

        for (Instruction instruction : procedure.getInstructions()) {
            for (InstructionOperand param : instruction.getElements()) {
                if (param instanceof ReferenceInstructionOperand refParam) {
                    InstructionReference reference = refParam.getReference();
                    if (reference.getType() == ReferenceType.VAR && seenVariables.add(reference)) {
                        int size = translation.getTypeByteSize(reference.getValueType());
                        stackSize += size; // ((size + 7) & ~7);
                        stackOffset.put(reference, stackSize);
                    }
                }
            }

            if (instruction.getOpCode() == OpCode.ALLOC) {
                // If we see a stack alloc instruction we will allocate x amount of bytes on the stack
                InstructionReference dst = instruction.getElementByIndex(0).asReference().getReference();

                if (dst.getType() == ReferenceType.VAR) {
                    // Add the size of the allocated stack
                    int size = instruction.getElementByIndex(1).size().getBits();
                    stackSize += size; // ((size + 7) & ~7);
                }
            }
        }
    }

}
