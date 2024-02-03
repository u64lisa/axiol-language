package axiol.target.assembly.arm;

import axiol.instruction.InstructionSet;
import axiol.target.AssemblyGenerator;


public class ARMAssemblyGenerator extends AssemblyGenerator {
    @Override
    public byte[] getAssembler(InstructionSet instructions) {
        return new byte[0];
    }

    @Override
    public void reset() {

    }
}
