package axiol.target.assembly.riscv;

public enum RiscVAssemblyRegister {
    zero, ra, sp, gp, tp, t0, t1, t2, s0, s1, a0, a1, a2, a3, a4, a5,
    a6, a7, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, t3, t4, t5, t6;

    public static final int BIT_32 = 4;
    public static final int BIT_64 = 8;

    //public String toString(InstructionReference ref) {
    //    return toString(RISC_V_Utils.getTypeByteSize(ref.getValueType()));
    //}
    //
    //public String toString(InstructionOperand param) {
    //    if (param instanceof ReferenceInstructionOperand ref) {
    //        return toString(RISC_V_Utils.getTypeByteSize(ref.getReference().getValueType()));
    //    } else {
    //        return toString(RISC_V_Utils.getTypeByteSize(param.getSize()));
    //    }
    //}

    public String toString(int bytes) {
        return switch (bytes) {
            case BIT_32 -> name();
            case BIT_64 -> name() + 'd';
            default -> throw new UnsupportedOperationException("No register found for bit size " + bytes);
        };
    }
}