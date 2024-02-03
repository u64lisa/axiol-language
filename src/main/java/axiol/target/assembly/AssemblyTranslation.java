package axiol.target.assembly;

import axiol.instruction.InstructionOperand;
import axiol.instruction.reference.InstructionReference;
import axiol.instruction.value.*;
import axiol.types.Type;

public interface AssemblyTranslation {
    String getPointerName(int size);

    String getParamValue(InstructionReference ref, AssemblyProgramElement proc);

    String getParamValue(InstructionOperand param, AssemblyProgramElement proc);

    String getRawStackPtr(InstructionReference ref, int offset, AssemblyProgramElement proc);

    String getStackPtr(InstructionReference ref, AssemblyProgramElement proc);

    String getPointerName(InstructionReference ref);

    int getLowerTypeSize(Type type);

    int getTypeSize(Type type);

    int getTypeByteSize(Type type);

    int getLowerTypeByteSize(Type type);

    default String toString(InstructionOperand operand) {
        if (operand instanceof BooleanInstructionOperand operandCasted) {
            return this.toString(operandCasted);
        }
        if (operand instanceof NumberInstructionOperand operandCasted) {
            return this.toString(operandCasted);
        }
        if (operand instanceof StringInstructionOperand operandCasted) {
            return this.toString(operandCasted);
        }
        if (operand instanceof UDTInstructionOperand operandCasted) {
            return this.toString(operandCasted);
        }
        if (operand instanceof ReferenceInstructionOperand operandCasted) {
            return this.toString(operandCasted);
        }
        throw new RuntimeException("unknown operand: %s".formatted(operand));
    }

    String toString(BooleanInstructionOperand operand);

    String toString(NumberInstructionOperand operand);

    String toString(ReferenceInstructionOperand operand);

    String toString(StringInstructionOperand operand);

    String toString(UDTInstructionOperand operand);

    default String escapeString(String string) {
        if (string == null) return null;

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);

            switch (c) { // Normal escapes
                case '\r':
                    sb.append("\\r");
                    continue;
                case '\n':
                    sb.append("\\n");
                    continue;
                case '\b':
                    sb.append("\\b");
                    continue;
                case '\t':
                    sb.append("\\t");
                    continue;
                case '\'':
                    sb.append("\\'");
                    continue;
                case '\"':
                    sb.append("\\\"");
                    continue;
                case '\\':
                    sb.append("\\\\");
                    continue;
            }

            if (c > 0xff) { // Unicode
                sb.append("\\u").append(toHexString(c, 4));
                continue;
            }

            if (Character.isISOControl(c)) { // Control character
                sb.append("\\x").append(toHexString(c, 2));
                continue;
            }

            sb.append(c);
        }

        return sb.toString();
    }

    default String toHexString(long value, int length) {
        if(length < 1) throw new IllegalArgumentException("The minimum length of the returned string cannot be less than one.");
        return String.format("%0" + length + "x", value);
    }

    int getPointerSize();
}
