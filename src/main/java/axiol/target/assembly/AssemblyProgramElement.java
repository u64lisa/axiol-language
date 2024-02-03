package axiol.target.assembly;

import axiol.instruction.ProgramElement;
import axiol.instruction.reference.InstructionReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AssemblyProgramElement {

    protected final Map<InstructionReference, Integer> stackOffset;
    protected final List<InstructionReference> params;
    protected final String name;
    protected int stackSize;

    public AssemblyProgramElement(ProgramElement procedure) {
        this.stackOffset = new HashMap<>();
        this.params = procedure.getParameters();
        this.name = procedure.getReference().getName();
    }

    public int getParamCount() {
        return params.size();
    }

    public InstructionReference getParam(int index) {
        return params.get(index);
    }

    public int getStackOffset(InstructionReference ref) {
        int element = stackOffset.get(ref);
        return stackOffset.get(ref);
    }

    public String getName() {
        return name;
    }

    public int getStackSize() {
        return stackSize;
    }

    public List<InstructionReference> getParams() {
        return params;
    }

    public Map<InstructionReference, Integer> getStackOffset() {
        return stackOffset;
    }
}
