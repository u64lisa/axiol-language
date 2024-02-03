package axiol.target.assembly.x86;

import axiol.instruction.InstructionOperand;
import axiol.instruction.reference.InstructionReference;
import axiol.instruction.value.ReferenceInstructionOperand;
import axiol.target.assembly.AssemblyTranslation;

public enum X86AssemblyRegister {
	AX, CX, DX, BX, SP, BP, SI, DI, R8, R9, R10, R11, R12, R13, R14, R15;
	
	public static final int
			BIT_128 = 16,
			BIT_64 = 8,
		BIT_32 = 4,
		BIT_16 = 2,
		BIT_8 = 1
				;
	
	public boolean isExtended() {
		return switch (this) {
			case R8, R9, R10, R11, R12, R13, R14, R15 -> true;
			default -> false;
		};
	}
	
	
	public String toString(AssemblyTranslation translation, InstructionReference ref) {
		return toString(translation.getTypeByteSize(ref.getValueType()));
	}
	
	public String toString(AssemblyTranslation translation, InstructionOperand param) {
		if (param instanceof ReferenceInstructionOperand ref) {
			return toString(translation.getTypeByteSize(ref.getReference().getValueType()));
		} else {
			return toString(translation.getTypeByteSize(param.asNumber().getType()));
		}
	}
	
	public String toString(int bytes) {
		if (bytes == 0) // default for pointer
			bytes = 8;

		String name = name();
		if (isExtended()) {
			return switch (bytes) {
				case BIT_128 -> "NOT IMPLEMENTED!"; // todo implement
				case BIT_64 -> name;
				case BIT_32 -> name + 'D';
				case BIT_16 -> name + 'W';
				case BIT_8 -> name + 'L';
				default -> throw new UnsupportedOperationException();
			};
		} else {
			return switch (bytes) {
				case BIT_128 -> "NOT IMPLEMENTED!"; // todo implement
				case BIT_64 -> 'R' + name;
				case BIT_32 -> 'E' + name;
				case BIT_16 -> name;
				case BIT_8 -> switch (this) {
					case AX, CX, DX, BX -> name.substring(0, 1) + 'L';
					default -> name + 'L';
				};
				default -> throw new UnsupportedOperationException("no register found in %s for bit size %s".formatted(name, bytes));
			};
		}
	}
}
