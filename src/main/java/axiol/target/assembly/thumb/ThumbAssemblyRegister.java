package axiol.target.assembly.thumb;

public enum ThumbAssemblyRegister {
    r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, sp, lr, pc,

    // 16-Bit-Register
    r0h, r1h, r2h, r3h, r4h, r5h, r6h, r7h, r8h, r9h, r10h, r11h, r12h, sph, lrh, pch;

    public static final int BIT_32 = 4;
    public static final int BIT_16 = 2;

    //public String toString(InstructionReference ref) {
    //    return toString(ThumbUtils.getTypeByteSize(ref.getValueType()));
    //}
    //
    //public String toString(InstructionOperand param) {
    //    if (param instanceof ReferenceInstructionOperand ref) {
    //        return toString(ThumbUtils.getTypeByteSize(ref.getReference().getValueType()));
    //    } else {
    //        return toString(ThumbUtils.getTypeByteSize(param.getSize()));
    //    }
    //}

    public String toString(int bytes) {
        return switch (bytes) {
            case BIT_32 -> "r" + (ordinal());
            case BIT_16 -> "r" + (ordinal()) + "h";
            default -> throw new UnsupportedOperationException("No register found for bit size " + bytes);
        };
    }
}