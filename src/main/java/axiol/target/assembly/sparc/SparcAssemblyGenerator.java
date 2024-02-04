package axiol.target.assembly.sparc;

import axiol.instruction.Instruction;
import axiol.instruction.InstructionSet;
import axiol.target.AssemblyGenerator;


public class SparcAssemblyGenerator extends AssemblyGenerator<SparcAssemblyProgramElement> {
    @Override
    public byte[] getAssembler(InstructionSet instructions) {
        return new byte[0];
    }

    @Override
    public String createNullTerminatedStrings() {
        return null;
    }

    @Override
    protected String buildInstruction(SparcAssemblyProgramElement proc, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitLabel(SparcAssemblyProgramElement proc, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitAlloc(SparcAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitInlineAssembly(SparcAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitMove(SparcAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitLoad(SparcAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitStore(SparcAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitExtending(SparcAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitReturn(SparcAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitOperators(SparcAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitShifting(SparcAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitComparison(SparcAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitCall(SparcAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitGotoIfNotEq(SparcAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitGotoIf(SparcAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitGoto(Instruction instruction) {
        return null;
    }

    @Override
    protected String emitNegate(SparcAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitDivide(SparcAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitMultiply(SparcAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitModulo(SparcAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    public void reset() {

    }
}
