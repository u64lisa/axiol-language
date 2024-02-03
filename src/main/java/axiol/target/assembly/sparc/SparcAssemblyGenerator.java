package axiol.target.assembly.sparc;

import axiol.instruction.InstructionSet;
import axiol.target.AssemblyGenerator;


public class SparcAssemblyGenerator extends AssemblyGenerator {
    @Override
    public byte[] getAssembler(InstructionSet instructions) {
        return new byte[0];
    }

    @Override
    public void reset() {

    }
}
