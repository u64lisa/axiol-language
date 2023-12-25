package axiol.parser.tree.statements.special;

import axiol.Architecture;
import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.TokenPosition;

import java.util.ArrayList;
import java.util.List;

public class NativeStatement extends Statement {

    private final TokenPosition position;

    @Override
    public TokenPosition position() {
        return position;
    }

    public enum Type {
        ASM,
        IR
    }

    private final Type type;
    private final Architecture architecture;
    private final List<NativeInstruction> instructions;

    public NativeStatement(TokenPosition position, Type type, Architecture architecture, List<NativeInstruction> instructions) {
        this.position = position;
        this.type = type;
        this.architecture = architecture;
        this.instructions = instructions;
    }

    public Type getType() {
        return type;
    }

    public List<NativeInstruction> getInstructions() {
        return instructions;
    }

    public Architecture getArchitecture() {
        return architecture;
    }

    @Override
    public NodeType type() {
        return NodeType.NATIVE_STATEMENT;
    }

    @Override
    public List<Statement> childStatements() {
        List<Statement> statements = new ArrayList<>();
        for (NativeInstruction instruction : this.instructions) {
            statements.addAll(instruction.parameters);
        }
        return statements;
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
