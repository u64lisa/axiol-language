package axiol.target.assembly.mips;

import axiol.instruction.InstructionSet;
import axiol.target.AssemblyGenerator;


public class MIPSAssemblyGenerator extends AssemblyGenerator {
    @Override
    public byte[] getAssembler(InstructionSet instructions) {
        return new byte[0];
    }

    @Override
    public void reset() {

    }
}
