package axiol.parser.tree.statements.oop;

import axiol.parser.statement.Accessibility;
import axiol.parser.statement.Parameter;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.tree.statements.BodyStatement;
import axiol.parser.util.error.TokenPosition;
import axiol.types.SimpleType;

import java.util.List;
import java.util.UUID;

public class FunctionStatement extends Statement {

    private final String name;
    private final Accessibility[] accessibility;
    private final List<Parameter> parameters;
    private final BodyStatement bodyStatement;
    private final SimpleType returnType;
    private final UUID uuid;

    public FunctionStatement(String name, Accessibility[] accessibility, List<Parameter> parameters, BodyStatement bodyStatement, SimpleType returnType, UUID uuid, TokenPosition position) {
        this.name = name;
        this.accessibility = accessibility;
        this.parameters = parameters;
        this.bodyStatement = bodyStatement;
        this.returnType = returnType;
        this.uuid = uuid;
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

    public SimpleType getReturnType() {
        return returnType;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Accessibility[] getAccessibility() {
        return accessibility;
    }
}
