package axiol.target.assembly.aarch;

import axiol.instruction.InstructionSet;
import axiol.target.AssemblyGenerator;


public class AARCHAssemblyGenerator extends AssemblyGenerator {
    @Override
    public byte[] getAssembler(InstructionSet instructions) {
        return new byte[0];
    }

    @Override
    public void reset() {

    }
}
