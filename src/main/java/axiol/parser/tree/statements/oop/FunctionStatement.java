package axiol.parser.tree.statements.oop;

import axiol.parser.statement.Accessibility;
import axiol.parser.statement.Parameter;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.tree.statements.BodyStatement;
import axiol.parser.util.error.TokenPosition;
import axiol.parser.util.reference.Reference;
import axiol.types.Type;

import java.util.List;

public class FunctionStatement extends Statement {

    private final String name;
    private final Accessibility[] accessibility;
    private final List<Parameter> parameters;
    private final BodyStatement bodyStatement;
    private final Type returnType;

    private final Reference reference;

    public FunctionStatement(String name, Accessibility[] accessibility, List<Parameter> parameters, BodyStatement bodyStatement,
                             Type returnType, Reference reference, TokenPosition position) {
        this.name = name;
        this.accessibility = accessibility;
        this.parameters = parameters;
        this.bodyStatement = bodyStatement;
        this.returnType = returnType;
        this.reference = reference;
        this.position = position;
    }

    private final TokenPosition position;

    @Override
    public TokenPosition position() {
        return position;
    }

    @Override
    public List<Statement> childStatements() {
        return List.of(bodyStatement);
    }

    @Override
    public NodeType type() {
        return NodeType.FUNCTION_STATEMENT;
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

    public Type getReturnType() {
        return returnType;
    }

    public Reference getReference() {
        return reference;
    }

    public Accessibility[] getAccessibility() {
        return accessibility;
    }
}
