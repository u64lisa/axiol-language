package axiol.target.assembly.arm;

public enum ARMAssemblyRegister {
    R0, R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, SP, LR, PC;

    public static final int BIT_64 = 8;
    public static final int BIT_32 = 4;
    public static final int BIT_16 = 2;
    public static final int BIT_8 = 1;

    //public String toString(InstructionReference ref) {
    //    return toString(ARMUtils.getTypeByteSize(ref.getValueType()));
    //}
    //
    //public String toString(InstructionOperand param) {
    //    if (param instanceof ReferenceInstructionOperand ref) {
    //        return toString(ARMUtils.getTypeByteSize(ref.getReference().getValueType()));
    //    } else {
    //        return toString(ARMUtils.getTypeByteSize(param.getSize()));
    //    }
    //}

    public String toString(int bytes) {
        return switch (bytes) {
            case BIT_64 -> "X" + (ordinal());
            case BIT_32 -> "R" + (ordinal());
            case BIT_16 -> "R" + (ordinal()) + "W";
            case BIT_8 -> "R" + (ordinal()) + "B";
            default -> throw new UnsupportedOperationException("No register found for bit size " + bytes);
        };
    }
}