package axiol.parser.tree.statements.special;

import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;

import java.util.ArrayList;
import java.util.List;

public class NativeStatement extends Statement {

    @Override
    public List<Statement> childStatements() {
        List<Statement> statements = new ArrayList<>();
        for (NativeInstruction instruction : this.instructions) {
            statements.addAll(instruction.parameters);
        }
        return statements;
    }

    public enum Type {
        ASM,
        IR
    }

    private final Type type;
    private final List<NativeInstruction> instructions;

    public NativeStatement(Type type, List<NativeInstruction> instructions) {
        this.type = type;
        this.instructions = instructions;
    }

    public Type getType() {
        return type;
    }

    public List<NativeInstruction> getInstructions() {
        return instructions;
    }

    public static class NativeInstruction {
        private final String line;
        private final List<Expression> parameters;

        public NativeInstruction(String line, List<Expression> parameters) {
            this.line = line;
            this.parameters = parameters;
        }

        public List<Expression> getParameters() {
            return parameters;
        }

        public String getLine() {
            return line;
        }
    }
}
