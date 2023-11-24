package axiol.parser.tree.statements.oop;

import axiol.parser.statement.Accessibility;
import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;
import axiol.parser.tree.statements.BodyStatement;
import axiol.types.ParsedType;

import java.util.List;

public class FunctionStatement extends Statement {

    private final String name;
    private final Accessibility[] accessibility;
    private final List<Parameter> parameters;
    private final BodyStatement bodyStatement;
    private final ParsedType returnType;

    public FunctionStatement(String name, Accessibility[] accessibility, List<Parameter> parameters, BodyStatement bodyStatement, ParsedType returnType) {
        this.name = name;
        this.accessibility = accessibility;
        this.parameters = parameters;
        this.bodyStatement = bodyStatement;
        this.returnType = returnType;
    }

    public static final class Parameter {
        private final String name;
        private final ParsedType parsedType;
        private final Expression defaultValue;
        private final boolean pointer;
        private final boolean referenced;

        public Parameter(String name, ParsedType parsedType, Expression defaultValue, boolean pointer, boolean referenced) {
            this.name = name;
            this.parsedType = parsedType;
            this.defaultValue = defaultValue;
            this.pointer = pointer;
            this.referenced = referenced;
        }

        public String getName() {
            return name;
        }

        public Expression getDefaultValue() {
            return defaultValue;
        }

        public ParsedType getParsedType() {
            return parsedType;
        }

        public boolean isPointer() {
            return pointer;
        }

        public boolean isReferenced() {
            return referenced;
        }

        @Override
        public String toString() {
            return "Parameter{" +
                    "name='" + name + '\'' +
                    ", parsedType=" + parsedType +
                    ", defaultValue=" + defaultValue +
                    ", pointer=" + pointer +
                    ", referenced=" + referenced +
                    '}';
        }
    }

    public String getName() {
        return name;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public BodyStatement getBodyStatement() {
        return bodyStatement;
    }

    public ParsedType getReturnType() {
        return returnType;
    }

    public Accessibility[] getAccessibility() {
        return accessibility;
    }
}
