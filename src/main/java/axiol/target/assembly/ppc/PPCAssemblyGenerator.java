package axiol.target.assembly.ppc;

import axiol.instruction.InstructionSet;
import axiol.target.AssemblyGenerator;


public class PPCAssemblyGenerator extends AssemblyGenerator {
    @Override
    public byte[] getAssembler(InstructionSet instructions) {
        return new byte[0];
    }

    @Override
    public void reset() {

    }
}
