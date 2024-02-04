package axiol.target.other.ir;

import axiol.instruction.Instruction;
import axiol.instruction.InstructionSet;
import axiol.target.AssemblyGenerator;

public class IRGenerator extends AssemblyGenerator<Void> {
	
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
	public String createNullTerminatedStrings() {
		return null;
	}

	@Override
	protected String buildInstruction(Void proc, Instruction instruction) {
		return null;
	}

	@Override
	protected String emitLabel(Void proc, Instruction instruction) {
		return null;
	}

	@Override
	protected String emitAlloc(Void procedure, Instruction instruction) {
		return null;
	}

	@Override
	protected String emitInlineAssembly(Void procedure, Instruction instruction) {
		return null;
	}

	@Override
	protected String emitMove(Void procedure, Instruction instruction) {
		return null;
	}

	@Override
	protected String emitLoad(Void procedure, Instruction instruction) {
		return null;
	}

	@Override
	protected String emitStore(Void procedure, Instruction instruction) {
		return null;
	}

	@Override
	protected String emitExtending(Void procedure, Instruction instruction) {
		return null;
	}

	@Override
	protected String emitReturn(Void procedure, Instruction instruction) {
		return null;
	}

	@Override
	protected String emitOperators(Void procedure, Instruction instruction) {
		return null;
	}

	@Override
	protected String emitShifting(Void procedure, Instruction instruction) {
		return null;
	}

	@Override
	protected String emitComparison(Void procedure, Instruction instruction) {
		return null;
	}

	@Override
	protected String emitCall(Void procedure, Instruction instruction) {
		return null;
	}

	@Override
	protected String emitGotoIfNotEq(Void procedure, Instruction instruction) {
		return null;
	}

	@Override
	protected String emitGotoIf(Void procedure, Instruction instruction) {
		return null;
	}

	@Override
	protected String emitGoto(Instruction instruction) {
		return null;
	}

	@Override
	protected String emitNegate(Void procedure, Instruction instruction) {
		return null;
	}

	@Override
	protected String emitDivide(Void procedure, Instruction instruction) {
		return null;
	}

	@Override
	protected String emitMultiply(Void procedure, Instruction instruction) {
		return null;
	}

	@Override
	protected String emitModulo(Void procedure, Instruction instruction) {
		return null;
	}

	@Override
	public void reset() {

	}
}
