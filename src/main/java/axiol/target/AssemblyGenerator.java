package axiol.target;

import axiol.instruction.InstructionSet;


public abstract class AssemblyGenerator {

	protected AssemblyGenerator() {
	}

	public abstract byte[] getAssembler(InstructionSet instructions);
	
	public abstract void reset();
}
