package axiol.target.other.ir;

import axiol.instruction.Instruction;
import axiol.instruction.InstructionSet;
import axiol.target.AssemblyGenerator;

public class IRGenerator extends AssemblyGenerator {
	
	@Override
	public byte[] getAssembler(InstructionSet program) {
		//		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		//
		//		try {
		//			//IRSerializer.write(program, bs);
		//		} catch (Exception e) {
		//			throw new CodeGenException("Failed to write ir code", e.getCause());
		//		}
		//
		//		return bs.toByteArray();
		return new byte[] { 1,3,3,7 };
	}
	
	@Override
	public void reset() {

	}
}
