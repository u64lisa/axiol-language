package axiol.target.assembly.ppc;

public enum PPCAssemblyRegister {
    r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15,
    r16, r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28, r29, r30, r31;

    public static final int BIT_32 = 4;
    public static final int BIT_64 = 8;

    //public String toString(InstructionReference ref) {
    //    return toString(PPCUtils.getTypeByteSize(ref.getValueType()));
    //}
    //
    //public String toString(InstructionOperand param) {
    //    if (param instanceof ReferenceInstructionOperand ref) {
    //        return toString(PPCUtils.getTypeByteSize(ref.getReference().getValueType()));
    //    } else {
    //        return toString(PPCUtils.getTypeByteSize(param.getSize()));
    //    }
    //}

    public String toString(int bytes) {
        return switch (bytes) {
            case BIT_32 -> "r" + (ordinal());
            case BIT_64 -> "r" + (ordinal()) + "l";
            default -> throw new UnsupportedOperationException("No register found for bit size " + bytes);
        };
    }
}