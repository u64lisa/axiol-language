package axiol.target.assembly.avr;

public enum AVRAssemblyRegister {
    R0, R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15,

    // 16-bit registers
    X, Y, Z,

    // 32-bit registers
    R16, R17, R18, R19, R20, R21, R22, R23, R24, R25, R26, R27, R28, R29, R30, R31;

    public static final int BIT_8 = 1;
    public static final int BIT_16 = 2;
    public static final int BIT_32 = 4;

    public String toString(int bits) {
        return switch (bits) {
            case BIT_8 -> "R" + (ordinal());
            case BIT_16 -> name();
            case BIT_32 -> "R" + (ordinal() + 16);
            default -> throw new UnsupportedOperationException("No register found for bit size " + bits);
        };
    }
}