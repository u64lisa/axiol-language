package axiol.instruction;

import axiol.parser.tree.RootNode;
import axiol.types.PrimitiveTypes;

public class InstructionGenerator {
    private final InstructionSetBuilder instructionSet;

    private int referenceId = 0;

    public InstructionGenerator() {
        instructionSet = new InstructionSetBuilder();
    }

    public InstructionSet generate(RootNode rootNode) {
        // todo process tree

        instructionSet.instruction(OpCode.ADD, builder -> builder
                .numberOperand(PrimitiveTypes.I8, 2)
                .numberOperand(PrimitiveTypes.U8, 2)
        );

        instructionSet.instruction(builder -> builder
                .opcode(OpCode.ADD)
                .numberOperand(PrimitiveTypes.I8, 2)
                .numberOperand(PrimitiveTypes.U8, 2)
        );

        return instructionSet.build();
    }

}
