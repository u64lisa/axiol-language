package axiol.target.assembly.x86;

import axiol.instruction.*;
import axiol.instruction.reference.InstructionReference;
import axiol.instruction.value.NumberInstructionOperand;
import axiol.instruction.value.ReferenceInstructionOperand;
import axiol.instruction.value.StringInstructionOperand;
import axiol.parser.util.reference.ReferenceType;
import axiol.target.AssemblyGenerator;
import axiol.target.assembly.AssemblyEmitContext;
import axiol.target.assembly.AssemblyEmitElement;
import axiol.target.assembly.AssemblyTranslation;
import axiol.target.assembly.AssemblyFilePrinter;


import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class X86AssemblyGenerator extends AssemblyGenerator<X86AssemblyProgramElement> {

    private static final boolean REG_PARAM = true;

    public final Map<String, byte[]> globalStrings = new LinkedHashMap<>();
    public final Map<String, String> labelStrings = new LinkedHashMap<>();

    private final AssemblyTranslation translation;

    public X86AssemblyGenerator() {
        this.translation = new X86AssemblyTranslation();
    }

    @Override
    public byte[] getAssembler(InstructionSet instructions) {
        AssemblyEmitContext context = new AssemblyEmitContext();

        InstructionReference main = null;
        for (ProgramElement proc : instructions.getInstructions()) {
            switch (proc.getType()) {
                case FUNCTION -> {
                    if (proc.getReference() == null)
                        continue;

                    X86AssemblyProgramElement asmProc = new X86AssemblyProgramElement(translation, proc);

                    InstructionReference test = proc.getReference();

                    if (test.getName().equals("main")) {
                        main = test;
                    }

                    for (Instruction instruction : proc.getInstructions()) {
                        context.append(buildInstruction(asmProc, instruction));
                        context.lineBreak();
                    }
                }

                case VARIABLE -> {
                    X86AssemblyProgramElement asmProc = new X86AssemblyProgramElement(translation, proc);

                    for (Instruction instruction : proc.getInstructions()) {
                        context.append(buildInstruction(asmProc, instruction));
                        context.lineBreak();
                    }
                }
                case ENUM -> {
                }
                case CLASS -> {
                }

                default -> {
                }
            }
        }

        if (main == null) {
            throw new RuntimeException("Main function was undefined");
        }

        AssemblyFilePrinter assemblyFilePrinter = new X86AssemblyFilePrinter();
        assemblyFilePrinter.init(64); // todo change in future
        assemblyFilePrinter.createEntryPoint(main.toSimpleString());
        assemblyFilePrinter.createCodeSection(context.build());
        assemblyFilePrinter.createDataSection(createNullTerminatedStrings());

        String source = assemblyFilePrinter.print();


        return source.trim().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String createNullTerminatedStrings() {
        StringBuilder dataSection = new StringBuilder();
        for (Map.Entry<String, byte[]> entry : globalStrings.entrySet()) {
            dataSection.append("    ").append(entry.getKey()).append(" db ");
            byte[] array = entry.getValue();

            for (int i = 0; i < array.length; i++) {
                if (i > 0) {
                    dataSection.append(", ");
                }

                int c = Byte.toUnsignedInt(array[i]);
                if (Character.isLetterOrDigit(c)) {
                    dataSection.append("'").append((char) c).append("'");
                } else {
                    dataSection.append("0x%02x".formatted(c));
                }
            }

            if (array.length > 0) {
                dataSection.append(", ");
            }

            dataSection.append("0\n");
        }

        return dataSection.toString();
    }

    @Override
    protected String buildInstruction(X86AssemblyProgramElement proc, Instruction instruction) {
        return switch (instruction.getOpCode()) {
            case LABEL -> emitLabel(proc, instruction);
            case ALLOC -> emitAlloc(proc, instruction);
            case CALL -> emitCall(proc, instruction);
            case STORE -> emitStore(proc, instruction);
            case LOAD -> emitLoad(proc, instruction);
            case MOVE -> emitMove(proc, instruction);
            case INLINE_ASSEMBLY -> emitInlineAssembly(proc, instruction);
            case GOTO -> emitGoto(instruction);
            case GOTO_IF -> emitGotoIf(proc, instruction);
            case GOTO_IF_NOT_EQ -> emitGotoIfNotEq(proc, instruction);
            case SIGN_EXTEND, ZERO_EXTEND,
                    BIG_ZERO_EXTEND, FLOATING_EXTEND -> emitExtending(proc, instruction);
            case ADD, SUB, AND, XOR, OR -> emitOperators(proc, instruction);
            case SHIFT_LEFT, SHIFT_RIGHT -> emitShifting(proc, instruction);

            case FLOATING_MODULO, SIGNED_MODULO, UNSIGNED_MODULO -> emitModulo(proc, instruction);
            case FLOATING_MULTIPLY, SIGNED_MULTIPLY, UNSIGNED_MULTIPLY -> emitMultiply(proc, instruction);
            case FLOATING_DIVIDE, SIGNED_DIVIDE, UNSIGNED_DIVIDE -> emitDivide(proc, instruction);

            case UNSIGNED_GREATER_THAN, UNSIGNED_LESS_THAN,
                    UNSIGNED_GREATER_THAN_EQUAL, UNSIGNED_LESS_THAN_EQUAL,
                    SIGNED_GREATER_THAN, SIGNED_LESS_THAN,
                    SIGNED_GREATER_THAN_EQUAL, SIGNED_LESS_THAN_EQUAL,
                    EQUALS, NEGATED_EQUALS -> emitComparison(proc, instruction);

            case RETURN -> emitReturn(proc, instruction);
            case NEGATE -> emitNegate(proc, instruction);

            case BIT_OR, BIG_TRUNCATE, FLOATING_ADD, FLOATING_EQUALS, FLOATING_GREATER_THAN,
                    FLOATING_GREATER_THAN_EQUAL, FLOATING_LESS_THAN, FLOATING_LESS_THAN_EQUAL, FLOATING_NEGATED_EQUALS,
                    FLOATING_SUB, INSTRUCTION_MODIFY, NEGATE_OR, SUBSTR, TERNARY, TRUNCATE, XOR_EQUAL -> null;

            default -> throw new IllegalStateException("unexpected opcode: %s".formatted(instruction.getOpCode()));
        };
    }

    @Override
    protected String emitLabel(X86AssemblyProgramElement proc, Instruction instruction) {
        InstructionReference reference = instruction.getElementByIndex(0).asReference().getReference();
        if (reference.getType() == ReferenceType.FUNCTION) {
            AssemblyEmitElement elements = new AssemblyEmitElement();
            elements.add("push RBP");
            elements.add("mov RBP, RSP");
            elements.add("sub RSP, 0x%x".formatted(proc.getStackSize()));
            elements.add("");

            // After that we use the stack
            X86AssemblyRegister[] regs = {X86AssemblyRegister.DI, X86AssemblyRegister.SI, X86AssemblyRegister.DX,
                    X86AssemblyRegister.CX, X86AssemblyRegister.R8, X86AssemblyRegister.R9};

            int offset = 16;
            for (int i = 0; i < proc.getParamCount(); i++) {
                InstructionReference param = proc.getParam(i);

                if (REG_PARAM && i < regs.length) {
                    if (param.getValueType().isVarargs()) {
                        String regName = X86AssemblyRegister.AX.toString(translation, param);
                        elements.add("lea %s, [RBP + 0x%x]".formatted(
                                regName,
                                offset
                        ));
                        elements.add("mov %s, %s".formatted(
                                translation.getParamValue(param, proc),
                                regName
                        ));
                    } else {
                        elements.add("mov %s, %s".formatted(
                                translation.getParamValue(param, proc),
                                regs[i].toString(translation, param)
                        ));
                    }
                } else {
                    String regName = X86AssemblyRegister.AX.toString(translation, param);
                    int size = translation.getTypeSize(param.getValueType());

                    if (param.getValueType().isVarargs()) {
                        elements.add("lea %s, [RBP + 0x%x]".formatted(
                                regName,
                                offset
                        ));
                    } else {
                        elements.add("mov %s, %s [RBP + 0x%x]".formatted(
                                regName,
                                translation.getPointerName(size),
                                offset
                        ));
                    }
                    elements.add("mov %s, %s".formatted(
                            translation.getParamValue(param, proc),
                            regName
                    ));

                    offset += (size >> 3);
                }
            }

            addLabelString(reference.toSimpleString(), reference.getPath());
            String label = reference.toSimpleString() + ":\n";
            return label + elements.stream().reduce("", (a, b) -> a + '\n' + b)
                    .indent(4).stripTrailing().replaceFirst(" {4}\n", "");
        }

        addLabelString(proc.getName() + "." + reference.toSimpleString(), "." + reference.toSimpleString());
        return "  ." + reference.toSimpleString() + ':';
    }

    @Override
    protected String emitAlloc(X86AssemblyProgramElement procedure, Instruction instruction) {
        AssemblyEmitElement elements = new AssemblyEmitElement();

        InstructionReference dst = instruction.getElementByIndex(0).asReference().getReference();
        int size = instruction.getElementByIndex(1).size().getBits();

        String regName = X86AssemblyRegister.AX.toString(translation, dst);
        elements.add("lea %s, %s".formatted(
                regName,
                translation.getRawStackPtr(dst, -size, procedure)
        ));
        elements.add("mov %s, %s".formatted(
                translation.getStackPtr(dst, procedure),
                regName
        ));

        return elements.export();
    }

    @Override
    protected String emitInlineAssembly(X86AssemblyProgramElement procedure, Instruction instruction) {
        AssemblyEmitElement elements = new AssemblyEmitElement();

        String targetType = instruction.getElementByIndex(0).asString().getValue();

        // We only inline assembly instructions
        if (!targetType.equals("asm")) {
            return elements.export();
        }

        String command = instruction.getElementByIndex(1).asString().getValue();
        for (int i = 2; i < instruction.getElements().size(); i++) {
            InstructionReference src = instruction.getElementByIndex(i).asReference().getReference();
            command = command.replaceFirst("\\{}", translation.getStackPtr(src, procedure));
        }

        elements.add(command);

        return elements.export();
    }

    @Override
    protected String emitMove(X86AssemblyProgramElement procedure, Instruction instruction) {
        AssemblyEmitElement elements = new AssemblyEmitElement();

        InstructionReference dst = instruction.getElementByIndex(0).asReference().getReference();
        InstructionOperand src = instruction.getElementByIndex(1);

        if (src instanceof NumberInstructionOperand value) {
            long number = value.getValue().longValue();

            String regName;
            // If the high 32 bits are set we need to store it in a register
            if ((number >>> 32) != 0) {
                elements.add("mov RAX, %s".formatted(value));
                regName = "RAX";
            } else {
                if (src.asNumber().getType().getBits() / 8 > 4 && ((number >> 31) & 1) != 0) {
                    elements.add("mov RAX, %s".formatted(value));
                    regName = "RAX";
                } else {
                    regName = translation.toString(value);
                }
            }

            elements.add("mov %s, %s".formatted(
                    translation.getStackPtr(dst, procedure),
                    regName
            ));
        } else if (src instanceof ReferenceInstructionOperand value) {
            String regName = X86AssemblyRegister.AX.toString(translation, value.getReference());
            elements.add("mov %s, %s".formatted(
                    regName,
                    translation.getStackPtr(value.getReference(), procedure)
            ));
            elements.add("mov %s, %s".formatted(
                    translation.getStackPtr(dst, procedure),
                    regName
            ));
        } else if (src instanceof StringInstructionOperand value) {
            String name = addGlobalString(value.getValue().getBytes());
            elements.add("mov %s, %s".formatted(
                    translation.getStackPtr(dst, procedure),
                    name
            ));
        }

        return elements.export();
    }

    @Override
    protected String emitLoad(X86AssemblyProgramElement procedure, Instruction instruction) {
        AssemblyEmitElement elements = new AssemblyEmitElement();

        InstructionReference dst = instruction.getElementByIndex(0).asReference().getReference();
        InstructionReference src = instruction.getElementByIndex(1).asReference().getReference();
        InstructionOperand offset = instruction.getElementByIndex(2);

        String offsetValue;
        if (offset instanceof NumberInstructionOperand value) {
            offsetValue = "0x%x".formatted(value.getValue().longValue() * translation.getLowerTypeByteSize(src.getValueType()));
        } else if (offset instanceof ReferenceInstructionOperand value) {
            int offsetSize = translation.getLowerTypeByteSize(src.getValueType());
            if (offsetSize > 8) {
                throw new RuntimeException();
            }

            offsetValue = "RCX * 0x%x".formatted(offsetSize);
            elements.add("xor RCX, RCX");
            elements.add("mov %s, %s".formatted(
                    X86AssemblyRegister.CX.toString(translation, value.getReference()),
                    translation.getStackPtr(value.getReference(), procedure)
            ));
        } else {
            throw new RuntimeException();
        }

        String regName = X86AssemblyRegister.AX.toString(translation, dst);
        elements.add("mov RBX, %s".formatted(
                translation.getParamValue(src, procedure)
        ));
        elements.add("mov %s, %s [RBX + %s]".formatted(
                regName,
                translation.getPointerName(dst),
                offsetValue
        ));
        elements.add("mov %s, %s".formatted(
                translation.getParamValue(dst, procedure),
                regName
        ));

        return elements.export();
    }

    @Override
    protected String emitStore(X86AssemblyProgramElement procedure, Instruction instruction) {
        AssemblyEmitElement elements = new AssemblyEmitElement();

        InstructionReference dst = instruction.getElementByIndex(0).asReference().getReference();
        InstructionOperand offset = instruction.getElementByIndex(1);
        InstructionOperand src = instruction.getElementByIndex(2);

        String srcValue = translation.getParamValue(src, procedure);
        elements.add("mov RBX, %s".formatted(
                translation.getStackPtr(dst, procedure)
        ));

        String offsetValue;
        if (offset instanceof NumberInstructionOperand value) {
            offsetValue = "0x%x".formatted(value.getValue().longValue() * translation.getLowerTypeByteSize(dst.getValueType()));
        } else if (offset instanceof ReferenceInstructionOperand value) {
            int offsetSize = translation.getLowerTypeByteSize(dst.getValueType());
            if (offsetSize > 8) {
                System.out.println(offsetSize);
                throw new RuntimeException();
            }

            offsetValue = "RCX * 0x%x".formatted(offsetSize);
            elements.add("xor RCX, RCX");
            elements.add("mov %s, %s".formatted(
                    X86AssemblyRegister.CX.toString(translation, value.getReference()),
                    translation.getStackPtr(value.getReference(), procedure)
            ));
        } else {
            throw new RuntimeException();
        }

        if (src instanceof NumberInstructionOperand) {
            elements.add("mov %s [RBX + %s], %s".formatted(
                    translation.getPointerName(dst.getValueType().getBits()),
                    offsetValue,
                    srcValue
            ));
        } else {
            String regName = X86AssemblyRegister.AX.toString(translation.getLowerTypeSize(dst.getValueType()) >> 3); // Size of one lower
            elements.add("mov %s, %s".formatted(
                    regName,
                    srcValue
            ));
            elements.add("mov %s [RBX + %s], %s".formatted(
                    translation.getPointerName(dst.getValueType().getBits()),
                    offsetValue,
                    regName
            ));
        }

        return elements.export();
    }

    @Override
    protected String emitExtending(X86AssemblyProgramElement procedure, Instruction instruction) {
        //  SIGN_EXTEND, ZERO_EXTEND, BIG_ZERO_EXTEND, FLOATING_EXTEND

        AssemblyEmitElement elements = new AssemblyEmitElement();

        InstructionOperand dst = instruction.getElementByIndex(0);
        InstructionOperand src = instruction.getElementByIndex(1);

        String regSrcName = X86AssemblyRegister.AX.toString(translation, instruction.getOpCode() == OpCode.SIGN_EXTEND ? dst : src);
        String regDstName = X86AssemblyRegister.AX.toString(translation, dst);

        if (dst.asNumber().getType().getBits() / 8 > src.asNumber().getType().getBits() / 8) {
            elements.add("xor RAX, RAX");
        }
        elements.add("mov%s %s, %s".formatted(
                instruction.getOpCode() == OpCode.SIGN_EXTEND ? "sx" : "",
                regSrcName,
                translation.getParamValue(src, procedure)
        ));
        elements.add("mov %s, %s".formatted(
                translation.getParamValue(dst, procedure),
                regDstName
        ));

        return elements.export();
    }

    @Override
    protected String emitReturn(X86AssemblyProgramElement procedure, Instruction instruction) {
        AssemblyEmitElement elements = new AssemblyEmitElement();

        if (instruction.getElements().size() == 1) {
            InstructionOperand param = instruction.getElementByIndex(0);

            if (param instanceof ReferenceInstructionOperand src) {
                String regName = X86AssemblyRegister.AX.toString(translation, src.getReference());
                elements.add("mov %s, %s".formatted(
                        regName,
                        translation.getStackPtr(src.getReference(), procedure)
                ));
            } else if (param instanceof NumberInstructionOperand num) {
                elements.add("mov RAX, %s".formatted(translation.toString(num)));
            } else {
                throw new RuntimeException();
            }
        }

        elements.add("mov RSP, RBP");
        elements.add("pop RBP");
        elements.add("ret");

        return elements.export();
    }

    @Override
    protected String emitOperators(X86AssemblyProgramElement procedure, Instruction instruction) {
        // ADD, SUB, AND, XOR, OR

        AssemblyEmitElement elements = new AssemblyEmitElement();

        InstructionReference dst = instruction.getElementByIndex(0).asReference().getReference();
        InstructionOperand value = instruction.getElementByIndex(1);

        String regAName = X86AssemblyRegister.AX.toString(translation, dst);
        elements.add("mov %s, %s".formatted(
                regAName,
                translation.getStackPtr(dst, procedure)
        ));

        String type = switch (instruction.getOpCode()) {
            case ADD -> "add";
            case SUB -> "sub";
            case AND -> "and";
            case XOR -> "xor";
            case OR -> "or";
            default -> throw new RuntimeException();
        };

        if (value instanceof NumberInstructionOperand numberInstructionOperand) {
            elements.add("%s %s, %s".formatted(
                    type,
                    regAName,
                    translation.toString(numberInstructionOperand)
            ));
        } else if (value instanceof ReferenceInstructionOperand referenceInstructionOperand) {
            elements.add("%s %s, %s".formatted(
                    type,
                    regAName,
                    translation.getParamValue(referenceInstructionOperand, procedure)
            ));
        } else {
            throw new RuntimeException();
        }

        elements.add("mov %s, %s".formatted(
                translation.getStackPtr(dst, procedure),
                regAName
        ));

        return elements.export();
    }

    @Override
    protected String emitShifting(X86AssemblyProgramElement procedure, Instruction instruction) {
        AssemblyEmitElement elements = new AssemblyEmitElement();

        InstructionReference dst = instruction.getElementByIndex(0).asReference().getReference();
        InstructionOperand value = instruction.getElementByIndex(1);

        String regAName = X86AssemblyRegister.AX.toString(translation, dst);
        String regCName = X86AssemblyRegister.CX.toString(translation, value);
        elements.add("xor RCX, RCX");
        elements.add("mov %s, %s".formatted(
                regCName,
                translation.getParamValue(value, procedure)
        ));
        elements.add("mov %s, %s".formatted(
                regAName,
                translation.getParamValue(dst, procedure)
        ));

        String type = switch (instruction.getOpCode()) {
            case SHIFT_RIGHT -> "shr";
            case SHIFT_LEFT -> "shl";
            default -> throw new RuntimeException();
        };
        elements.add("%s %s, CL".formatted(type, regAName));
        elements.add("mov %s, %s".formatted(
                translation.getParamValue(dst, procedure),
                regAName
        ));

        return elements.export();
    }

    @Override
    protected String emitComparison(X86AssemblyProgramElement procedure, Instruction instruction) {
        AssemblyEmitElement elements = new AssemblyEmitElement();

        InstructionReference dst = instruction.getElementByIndex(0).asReference().getReference();
        InstructionReference value = instruction.getElementByIndex(1).asReference().getReference();

        String regAName = X86AssemblyRegister.AX.toString(translation, dst);
        String regBName = X86AssemblyRegister.BX.toString(translation, value);
        String regACmov = X86AssemblyRegister.AX.toString(translation, dst);
        String regCCmov = X86AssemblyRegister.CX.toString(translation, dst);
        if (translation.getTypeByteSize(dst.getValueType()) == 1) {
            regACmov = X86AssemblyRegister.AX.toString(2);
            regCCmov = X86AssemblyRegister.CX.toString(2);
        }

        elements.add("mov RCX, 1");
        elements.add("mov RAX, 0");
        elements.add("mov %s, %s".formatted(
                regBName,
                translation.getParamValue(value, procedure)
        ));
        elements.add("cmp %s, %s".formatted(
                translation.getStackPtr(dst, procedure),
                regBName
        ));

        String type = switch (instruction.getOpCode()) {
            case UNSIGNED_GREATER_THAN -> "a";
            case UNSIGNED_LESS_THAN -> "b";
            case UNSIGNED_GREATER_THAN_EQUAL -> "ae";
            case UNSIGNED_LESS_THAN_EQUAL -> "be";

            case SIGNED_GREATER_THAN -> "g";
            case SIGNED_LESS_THAN -> "l";
            case SIGNED_GREATER_THAN_EQUAL -> "ge";
            case SIGNED_LESS_THAN_EQUAL -> "le";

            case EQUALS -> "e";
            case NEGATED_EQUALS -> "ne";
            default -> throw new RuntimeException();
        };
        elements.add("cmov%s %s, %s".formatted(type, regACmov, regCCmov));
        elements.add("mov %s, %s".formatted(
                translation.getStackPtr(dst, procedure),
                regAName
        ));

        return elements.export();
    }

    @Override
    protected String emitCall(X86AssemblyProgramElement procedure, Instruction instruction) {
        AssemblyEmitElement elements = new AssemblyEmitElement();

        InstructionReference dst = instruction.getElementByIndex(0).asReference().getReference();
        InstructionReference fun = instruction.getElementByIndex(1).asReference().getReference();

        if (fun.getIdent() == null) {
            throw new RuntimeException("Function '" + fun + "' is unidentifiable!");
        }

        // todo get theses infos from instruction
        boolean isVararg = false;
        int maxParam = 1;

        X86AssemblyRegister[] regs = {
                X86AssemblyRegister.DI, X86AssemblyRegister.SI, X86AssemblyRegister.DX,
                X86AssemblyRegister.CX, X86AssemblyRegister.R8, X86AssemblyRegister.R9
        };

        int offset = 0;
        for (int i = 0; i < instruction.getElements().size() - 2; i++) {
            InstructionOperand param = instruction.getElementByIndex(i + 2);

            if (REG_PARAM) {
                if (i >= maxParam && isVararg || i >= regs.length) {
                    int size = translation.getTypeSize(param.asNumber().getType());
                    offset += (size >> 3);
                }
            } else {
                int size = translation.getTypeSize(param.asNumber().getType());
                offset += (size >> 3);
            }
        }

        if (offset != 0) {
            elements.add("sub RSP, 0x%x".formatted(offset));
        }

        for (int i = 0, pOffset = 0; i < instruction.getElements().size() - 2; i++) {
            InstructionOperand param = instruction.getElementByIndex(i + 2);
            int size = translation.getTypeSize(param.asNumber().getType());

            if (REG_PARAM && i < regs.length && i < maxParam) {
                elements.add("mov %s, %s".formatted(
                        regs[i].toString(translation, param),
                        translation.getParamValue(param, procedure)
                ));
            } else {
                String regName = X86AssemblyRegister.AX.toString(translation, param);
                elements.add("mov %s, %s".formatted(
                        regName,
                        translation.getParamValue(param, procedure)
                ));
                elements.add("mov %s [RSP + 0x%x], %s".formatted(
                        translation.getPointerName(size),
                        pOffset,
                        regName
                ));

                pOffset += (size >> 3);
            }
        }
        elements.add("call %s".formatted(fun.toSimpleString()));

        if (offset != 0) {
            elements.add("add RSP, 0x%x".formatted(offset));
        }

        if (dst.getValueType().getBits() != 0) {
            String regName = X86AssemblyRegister.AX.toString(translation, dst);
            elements.add("mov %s, %s".formatted(
                    translation.getStackPtr(dst, procedure),
                    regName
            ));
        }

        return elements.export();
    }

    @Override
    protected String emitGotoIfNotEq(X86AssemblyProgramElement procedure, Instruction instruction) {
        AssemblyEmitElement elements = new AssemblyEmitElement();

        InstructionReference src = instruction.getElementByIndex(0).asReference().getReference();
        InstructionReference dst = instruction.getElementByIndex(1).asReference().getReference();

        String regName = X86AssemblyRegister.AX.toString(translation, src);
        elements.add("mov %s, %s".formatted(
                translation.getStackPtr(src, procedure),
                regName
        ));
        elements.add("test %s, %s".formatted(regName, regName));
        elements.add("jnz .%s".formatted(dst.toSimpleString()));

        return elements.export();
    }

    @Override
    protected String emitGotoIf(X86AssemblyProgramElement procedure, Instruction instruction) {
        AssemblyEmitElement elements = new AssemblyEmitElement();

        InstructionReference src = instruction.getElementByIndex(0).asReference().getReference();
        InstructionReference dst = instruction.getElementByIndex(1).asReference().getReference();

        String regName = X86AssemblyRegister.AX.toString(translation, src);
        elements.add("mov %s, %s".formatted(
                translation.getStackPtr(src, procedure),
                regName
        ));
        elements.add("test %s, %s".formatted(regName, regName));
        elements.add("jz .%s".formatted(dst.toSimpleString()));

        return elements.export();
    }

    @Override
    protected String emitGoto(Instruction instruction) {
        AssemblyEmitElement elements = new AssemblyEmitElement();

        InstructionReference dst = instruction.getElementByIndex(0).asReference().getReference();
        elements.add("jmp .%s".formatted(dst.toSimpleString()));

        return elements.export();
    }

    @Override
    protected String emitNegate(X86AssemblyProgramElement procedure, Instruction instruction) {
        AssemblyEmitElement elements = new AssemblyEmitElement();

        InstructionReference dst = instruction.getElementByIndex(0).asReference().getReference();
        InstructionOperand src = instruction.getElementByIndex(1);

        String regName = X86AssemblyRegister.AX.toString(translation, src);
        elements.add("mov %s, %s".formatted(
                regName,
                translation.getParamValue(src, procedure)
        ));
        elements.add("neg %s".formatted(regName));
        elements.add("mov %s, %s".formatted(
                translation.getStackPtr(dst, procedure),
                regName
        ));

        return elements.export();
    }

    @Override
    protected String emitDivide(X86AssemblyProgramElement procedure, Instruction instruction) {
        AssemblyEmitElement elements = new AssemblyEmitElement();

        boolean unsigned = instruction.getOpCode() == OpCode.UNSIGNED_DIVIDE;
        InstructionReference dst = instruction.getElementByIndex(0).asReference().getReference();
        InstructionOperand value = instruction.getElementByIndex(1);

        String regAName = X86AssemblyRegister.AX.toString(translation, dst);
        elements.add("xor RDX, RDX");
        elements.add("mov %s, %s".formatted(
                regAName,
                translation.getParamValue(dst, procedure)
        ));
        elements.add("%s %s".formatted(
                unsigned ? "div" : "idiv",
                translation.getParamValue(value, procedure)
        ));
        elements.add("mov %s, %s".formatted(
                translation.getStackPtr(dst, procedure),
                regAName
        ));

        return elements.export();
    }

    @Override
    protected String emitMultiply(X86AssemblyProgramElement procedure, Instruction instruction) {
        AssemblyEmitElement elements = new AssemblyEmitElement();

        boolean unsigned = instruction.getOpCode() == OpCode.UNSIGNED_MULTIPLY;
        InstructionReference dst = instruction.getElementByIndex(0).asReference().getReference();
        InstructionOperand value = instruction.getElementByIndex(1);

        String regAName = X86AssemblyRegister.AX.toString(translation, dst);
        elements.add("xor RDX, RDX");
        elements.add("mov %s, %s".formatted(
                regAName,
                translation.getParamValue(dst, procedure)
        ));
        elements.add("%s %s".formatted(
                unsigned ? "mul" : "imul",
                translation.getParamValue(value, procedure)
        ));
        elements.add("mov %s, %s".formatted(
                translation.getStackPtr(dst, procedure),
                regAName
        ));

        return elements.export();
    }

    @Override
    protected String emitModulo(X86AssemblyProgramElement procedure, Instruction instruction) {
        AssemblyEmitElement elements = new AssemblyEmitElement();

        boolean unsigned = instruction.getOpCode() == OpCode.UNSIGNED_MODULO;
        InstructionReference dst = instruction.getElementByIndex(0).asReference().getReference();
        InstructionOperand value = instruction.getElementByIndex(1);

        String regAName = X86AssemblyRegister.AX.toString(translation, dst);
        String remName = X86AssemblyRegister.DX.toString(translation, dst);
        elements.add("xor RDX, RDX");
        elements.add("mov %s, %s".formatted(
                regAName,
                translation.getParamValue(dst, procedure)
        ));
        elements.add("%s %s".formatted(
                unsigned ? "div" : "idiv",
                translation.getParamValue(value, procedure)
        ));
        elements.add("mov %s, %s".formatted(
                translation.getStackPtr(dst, procedure),
                remName
        ));

        return elements.export();
    }

    public String addGlobalString(byte[] content) {
        int index = globalStrings.size();
        String name = "global_string_" + index;
        globalStrings.put(name, content);
        return name;
    }

    public void addLabelString(String label, String name) {
        labelStrings.put(label, name);
    }

    @Override
    public void reset() {

    }
}
