package axiol.target.assembly.mips;

public enum MIPSAssemblyRegister {
    $zero, $at, $v0, $v1, $a0, $a1, $a2, $a3, $t0, $t1, $t2, $t3, $t4, $t5, $t6, $t7,
    $s0, $s1, $s2, $s3, $s4, $s5, $s6, $s7, $t8, $t9, $k0, $k1, $gp, $sp, $fp, $ra,

    $x0, $x1, $x2, $x3, $x4, $x5, $x6, $x7, $x8, $x9, $x10, $x11, $x12, $x13, $x14, $x15,
    $x16, $x17, $x18, $x19, $x20, $x21, $x22, $x23, $x24, $x25, $x26, $x27, $x28, $x29, $x30, $x31;


    public static final int BIT_64 = 8;
    public static final int BIT_32 = 4;

    //public String toString(InstructionReference ref) {
    //    return toString(MIPSUtils.getTypeByteSize(ref.getValueType()));
    //}
    //
    //public String toString(InstructionOperand param) {
    //    if (param instanceof ReferenceInstructionOperand ref) {
    //        return toString(MIPSUtils.getTypeByteSize(ref.getReference().getValueType()));
    //    } else {
    //        return toString(MIPSUtils.getTypeByteSize(param.getSize()));
    //    }
    //}

    public String toString(int bytes) {
        return switch (bytes) {
            case BIT_64 -> "$" + (ordinal() - 32);
            case BIT_32 -> name();
            default -> throw new UnsupportedOperationException("No register found for bit size " + bytes);
        };
    }
}