package axiol.target.assembly.arm;

import axiol.instruction.Instruction;
import axiol.instruction.InstructionSet;
import axiol.instruction.ProgramElement;
import axiol.instruction.reference.InstructionReference;
import axiol.target.AssemblyGenerator;
import axiol.target.assembly.AssemblyEmitContext;
import axiol.target.assembly.AssemblyFilePrinter;
import axiol.target.assembly.AssemblyTranslation;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;


public class ARMAssemblyGenerator extends AssemblyGenerator<ARMAssemblyProgramElement> {

    private static final boolean REG_PARAM = true;

    public final Map<String, byte[]> globalStrings = new LinkedHashMap<>();
    public final Map<String, String> labelStrings = new LinkedHashMap<>();

    private final AssemblyTranslation translation;

    public ARMAssemblyGenerator() {
        this.translation = new ARMAssemblyTranslation();
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

                    ARMAssemblyProgramElement asmProc = new ARMAssemblyProgramElement(translation, proc);

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
                    ARMAssemblyProgramElement asmProc = new ARMAssemblyProgramElement(translation, proc);

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

        AssemblyFilePrinter assemblyFilePrinter = new ARMAssemblyFilePrinter();
        assemblyFilePrinter.init(64); // todo change in future
        assemblyFilePrinter.createEntryPoint(main.toSimpleString());
        assemblyFilePrinter.createCodeSection(context.build());
        assemblyFilePrinter.createDataSection(createNullTerminatedStrings());

        String source = assemblyFilePrinter.print();


        return source.trim().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String createNullTerminatedStrings() {
        return null;
    }


    @Override
    protected String buildInstruction(ARMAssemblyProgramElement proc, Instruction instruction) {
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
    protected String emitLabel(ARMAssemblyProgramElement proc, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitAlloc(ARMAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitInlineAssembly(ARMAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitMove(ARMAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitLoad(ARMAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitStore(ARMAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitExtending(ARMAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitReturn(ARMAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitOperators(ARMAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitShifting(ARMAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitComparison(ARMAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitCall(ARMAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitGotoIfNotEq(ARMAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitGotoIf(ARMAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitGoto(Instruction instruction) {
        return null;
    }

    @Override
    protected String emitNegate(ARMAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitDivide(ARMAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitMultiply(ARMAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }

    @Override
    protected String emitModulo(ARMAssemblyProgramElement procedure, Instruction instruction) {
        return null;
    }


    @Override
    public void reset() {

    }
}
