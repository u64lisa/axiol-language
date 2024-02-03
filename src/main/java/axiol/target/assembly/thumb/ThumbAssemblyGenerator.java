package axiol.target.assembly.thumb;

import axiol.instruction.InstructionSet;
import axiol.target.AssemblyGenerator;


public class ThumbAssemblyGenerator extends AssemblyGenerator {
    @Override
    public byte[] getAssembler(InstructionSet instructions) {
        return new byte[0];
    }

    @Override
    public void reset() {

    }
}
