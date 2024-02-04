package axiol.target.assembly.mips;

import axiol.instruction.Instruction;
import axiol.instruction.InstructionSet;
import axiol.target.AssemblyGenerator;


public class MIPSAssemblyGenerator extends AssemblyGenerator<MIPSAssemblyProgramElement> {
    @Override
    public byte[] getAssembler(InstructionSet instructions) {
        return new byte[0];
    }

    @Override
    public String createNullTerminatedStrings() {
        return null;
    }

    @Override
    protected String buildInstruction(MIPSAssemblyProgramElement proc, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitLabel(MIPSAssemblyProgramElement proc, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitAlloc(MIPSAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitInlineAssembly(MIPSAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitMove(MIPSAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitLoad(MIPSAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitStore(MIPSAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitExtending(MIPSAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitReturn(MIPSAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitOperators(MIPSAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitShifting(MIPSAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitComparison(MIPSAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitCall(MIPSAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitGotoIfNotEq(MIPSAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitGotoIf(MIPSAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitGoto(Instruction instruction) {
        return null;
    }

    @Override
    protected String emitNegate(MIPSAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitDivide(MIPSAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitMultiply(MIPSAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitModulo(MIPSAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    public void reset() {

    }
}
