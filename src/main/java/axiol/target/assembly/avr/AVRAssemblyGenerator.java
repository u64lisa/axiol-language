package axiol.target.assembly.avr;

import axiol.instruction.InstructionSet;
import axiol.target.AssemblyGenerator;


public class AVRAssemblyGenerator extends AssemblyGenerator {

    @Override
    public byte[] getAssembler(InstructionSet instructions) {
        return new byte[0];
    }

    @Override
    public void reset() {

    }
}
