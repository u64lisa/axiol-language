package axiol.target.assembly.arm;

import axiol.instruction.InstructionOperand;
import axiol.instruction.reference.InstructionReference;
import axiol.instruction.value.*;
import axiol.types.Type;
import axiol.target.assembly.AssemblyProgramElement;
import axiol.target.assembly.AssemblyTranslation;

class ARMAssemblyTranslation implements AssemblyTranslation {

    @Override
    public String getPointerName(int size) {
        return switch (size) {
            case 8 -> "byte";
            case 16 -> "hword";
            case 32 -> "word";
            case 0, 64 -> "quad";
            default -> throw new RuntimeException("invalid pointer size for %s".formatted(size));
        };
    }

    @Override
    public String getParamValue(InstructionReference ref, AssemblyProgramElement proc) {
        return getStackPtr(ref, proc);
    }

    @Override
    public String getParamValue(InstructionOperand param, AssemblyProgramElement proc) {
        if (param instanceof ReferenceInstructionOperand value) {
            return getStackPtr(value.getReference(), proc);
        } else if (param instanceof NumberInstructionOperand value) {
            return value.toString();
        }

        throw new RuntimeException();
    }

    @Override
    public String getRawStackPtr(InstructionReference ref, int offset, AssemblyProgramElement proc) {
        return "[SP, #0x%x]".formatted(offset);
    }

    @Override
    public String getStackPtr(InstructionReference ref, AssemblyProgramElement proc) {
        return "%s [SP, #0x%x]".formatted(getPointerName(ref), proc.getStackOffset(ref));
    }

    @Override
    public String getPointerName(InstructionReference ref) {
        return getPointerName(getTypeSize(ref.getValueType()));
    }

    @Override
    public int getLowerTypeSize(Type type) {
        return ((type.getArrayDepth() > 1) ? getPointerSize() : (type.getBits() >> 3)) << 3;
    }

    @Override
    public int getTypeSize(Type type) {
        return getTypeByteSize(type) << 3;
    }

    @Override
    public int getTypeByteSize(Type type) {
        return (type.getArrayDepth() > 0) ? getPointerSize() : (type.getBits() >> 3);
    }

    @Override
    public int getLowerTypeByteSize(Type type) {
        return ((type.getArrayDepth() > 1) ? getPointerSize() : (type.getBits() >> 3));
    }

    @Override
    public String toString(BooleanInstructionOperand operand) {
        return null; // todo write this
    }

    @Override
    public String toString(NumberInstructionOperand instNumber) {
        int size = instNumber.getType().getBits();
        if (instNumber.getType().isFloating()) {
            return switch (size) {
                case 64 -> Double.toString(Double.longBitsToDouble(instNumber.getValue().longValue()));
                case 32 -> Float.toString(Float.intBitsToFloat((int) instNumber.getValue()));
                default -> throw new RuntimeException("Invalid float size %s".formatted(size));
            };
        }

        if (instNumber.getType().isUnsigned()) {
            return switch (size) {
                case 64 -> Long.toUnsignedString(instNumber.getValue().longValue());
                case 32 -> Long.toUnsignedString(instNumber.getValue().longValue() & 0xFFFFFFFFL);
                case 16 -> Integer.toUnsignedString((int) instNumber.getValue() & 0xFFFF);
                case 8 -> Integer.toUnsignedString((int) instNumber.getValue() & 0xFF);
                default -> throw new RuntimeException("Invalid unsigned size %s".formatted(size));
            };
        }

        return switch (size) {
            case 64 -> Long.toString(instNumber.getValue().longValue());
            case 32 -> Integer.toString((int) instNumber.getValue());
            case 16 -> Short.toString((short) instNumber.getValue());
            case 8 -> Byte.toString((byte) instNumber.getValue());
            default -> throw new RuntimeException("Invalid integer size %s".formatted(size));
        };
    }

    @Override
    public String toString(StringInstructionOperand instNumber) {
        return "\"%s\"".formatted(escapeString(instNumber.getValue()));
    }

    @Override
    public String toString(UDTInstructionOperand operand) {
        return null; // todo write this
    }

    @Override
    public String toString(ReferenceInstructionOperand instNumber) {
        return instNumber.getReference().toString();
    }

    @Override
    public int getPointerSize() {
        return 8; // ARM typically uses 32-bit pointers
    }
}