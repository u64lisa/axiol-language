package axiol.target.assembly.aarch;

public enum AArchAssemblyRegister {
    X0, X1, X2, X3, X4, X5, X6, X7, X8, X9, X10, X11, X12, X13, X14, X15,
    X16, X17, X18, X19, X20, X21, X22, X23, X24, X25, X26, X27, X28, X29, X30;

    public static final int BIT_64 = 8,
            BIT_32 = 4,
            BIT_16 = 2,
            BIT_8 = 1;

   // public String toString(InstructionReference ref) {
   //     return toString(AArch64Utils.getTypeByteSize(ref.getValueType()));
   // }
   //
   // public String toString(InstructionOperand param) {
   //     if (param instanceof ReferenceInstructionOperand ref) {
   //         return toString(AArch64Utils.getTypeByteSize(ref.getReference().getValueType()));
   //     } else {
   //         return toString(AArch64Utils.getTypeByteSize(param.getSize()));
   //     }
   // }

   public String toString(int bytes) {
       return switch (bytes) {
           case BIT_64 -> "X" + ordinal();
           case BIT_32 -> "W" + ordinal();
           case BIT_16 -> "H" + ordinal();
           case BIT_8 -> "B" + ordinal();
           default -> throw new UnsupportedOperationException("No register found for bit size " + bytes);
       };
   }
}