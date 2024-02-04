package axiol.target.assembly.aarch;

import axiol.instruction.Instruction;
import axiol.instruction.InstructionSet;
import axiol.target.AssemblyGenerator;


public class AARCHAssemblyGenerator extends AssemblyGenerator<AARCHAssemblyProgramElement> {
    @Override
    public byte[] getAssembler(InstructionSet instructions) {
        return new byte[0];
    }

    @Override
    public String createNullTerminatedStrings() {
        return null;
    }

    @Override
    protected String buildInstruction(AARCHAssemblyProgramElement proc, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitLabel(AARCHAssemblyProgramElement proc, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitAlloc(AARCHAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitInlineAssembly(AARCHAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitMove(AARCHAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitLoad(AARCHAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitStore(AARCHAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitExtending(AARCHAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitReturn(AARCHAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitOperators(AARCHAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitShifting(AARCHAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitComparison(AARCHAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitCall(AARCHAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitGotoIfNotEq(AARCHAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitGotoIf(AARCHAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitGoto(Instruction instruction) {
        return null;
    }

    @Override
    protected String emitNegate(AARCHAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitDivide(AARCHAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitMultiply(AARCHAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitModulo(AARCHAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    public void reset() {

    }
}
