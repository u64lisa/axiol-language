package axiol.target.assembly.sparc;

public enum SparcAssemblyRegister {
    g0, g1, g2, g3, g4, g5, g6, g7, o0, o1, o2, o3, o4, o5, o6, o7,
    l0, l1, l2, l3, l4, l5, l6, l7, i0, i1, i2, i3, i4, i5, i6, i7;

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

    public String toString(int bits) {
        return switch (bits) {
            case BIT_32 -> name();
            case BIT_64 -> name() + 'd';
            default -> throw new UnsupportedOperationException("No register found for bit size " + bits);
        };
    }
}