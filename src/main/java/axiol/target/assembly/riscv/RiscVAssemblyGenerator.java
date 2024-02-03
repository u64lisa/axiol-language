package axiol.target.assembly.riscv;

import axiol.instruction.InstructionSet;
import axiol.target.AssemblyGenerator;


public class RiscVAssemblyGenerator extends AssemblyGenerator {
    @Override
    public byte[] getAssembler(InstructionSet instructions) {
        return new byte[0];
    }

    @Override
    public void reset() {

    }
}
