package axiol.target;

import axiol.instruction.Instruction;
import axiol.instruction.InstructionSet;


public abstract class AssemblyGenerator<T> {

    protected AssemblyGenerator() {
    }

    public abstract byte[] getAssembler(InstructionSet instructions);

    public abstract String createNullTerminatedStrings();

    protected abstract String buildInstruction(T proc, Instruction instruction);

    protected abstract String emitLabel(T proc, Instruction instruction);

    protected abstract String emitAlloc(T procedure, Instruction instruction);

    protected abstract String emitInlineAssembly(T procedure, Instruction instruction);

    protected abstract String emitMove(T procedure, Instruction instruction);

    protected abstract String emitLoad(T procedure, Instruction instruction);

    protected abstract String emitStore(T procedure, Instruction instruction);

    protected abstract String emitExtending(T procedure, Instruction instruction);

    protected abstract String emitReturn(T procedure, Instruction instruction);

    protected abstract String emitOperators(T procedure, Instruction instruction);

    protected abstract String emitShifting(T procedure, Instruction instruction);

    protected abstract String emitComparison(T procedure, Instruction instruction);

    protected abstract String emitCall(T procedure, Instruction instruction);

    protected abstract String emitGotoIfNotEq(T procedure, Instruction instruction);

    protected abstract String emitGotoIf(T procedure, Instruction instruction);

    protected abstract String emitGoto(Instruction instruction);

    protected abstract String emitNegate(T procedure, Instruction instruction);

    protected abstract String emitDivide(T procedure, Instruction instruction);

    protected abstract String emitMultiply(T procedure, Instruction instruction);

    protected abstract String emitModulo(T procedure, Instruction instruction);

    public abstract void reset();
}
