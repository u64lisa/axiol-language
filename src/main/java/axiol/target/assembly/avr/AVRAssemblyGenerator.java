package axiol.target.assembly.avr;

import axiol.instruction.Instruction;
import axiol.instruction.InstructionSet;
import axiol.target.AssemblyGenerator;


public class AVRAssemblyGenerator extends AssemblyGenerator<AVRAssemblyProgramElement> {

    @Override
    public byte[] getAssembler(InstructionSet instructions) {
        return new byte[0];
    }

    @Override
    public String createNullTerminatedStrings() {
        return null;
    }

    @Override
    protected String buildInstruction(AVRAssemblyProgramElement proc, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitLabel(AVRAssemblyProgramElement proc, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitAlloc(AVRAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitInlineAssembly(AVRAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitMove(AVRAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitLoad(AVRAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitStore(AVRAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitExtending(AVRAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitReturn(AVRAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitOperators(AVRAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitShifting(AVRAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitComparison(AVRAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitCall(AVRAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitGotoIfNotEq(AVRAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitGotoIf(AVRAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitGoto(Instruction instruction) {
        return null;
    }

    @Override
    protected String emitNegate(AVRAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitDivide(AVRAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitMultiply(AVRAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitModulo(AVRAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    public void reset() {

    }
}
