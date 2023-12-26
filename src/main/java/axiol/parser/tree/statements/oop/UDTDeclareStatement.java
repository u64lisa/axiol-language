package axiol.parser.tree.statements.oop;

import axiol.parser.statement.UDTType;
import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.util.error.TokenPosition;
import axiol.types.Reference;

import java.util.ArrayList;
import java.util.List;

public class UDTDeclareStatement extends Statement {

    private final String typeName;
    private final String referenceName;
    private final List<Expression> parameters;

    private final Reference reference;

    // needs to be set by @StaticAnalysis
    private UDTType type;

    public UDTDeclareStatement(String typeName, String referenceName, List<Expression> parameters, Reference reference, TokenPosition position) {
        this.typeName = typeName;
        this.referenceName = referenceName;
        this.parameters = parameters;
        this.reference = reference;
        this.position = position;
    }

    private final TokenPosition position;

    @Override
    public TokenPosition position() {
        return position;
    }

    @Override
    public NodeType type() {
        return NodeType.UDT_DECLARE_STATEMENT;
    }

    @Override
    public List<Statement> childStatements() {
        return new ArrayList<>(parameters);
    }

    public String getTypeName() {
        return typeName;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public Reference getReference() {
        return reference;
    }

    public List<Expression> getParameters() {
        return parameters;
    }
}
