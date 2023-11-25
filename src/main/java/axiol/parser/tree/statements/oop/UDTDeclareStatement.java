package axiol.parser.tree.statements.oop;

import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;

import java.util.ArrayList;
import java.util.List;

public class UDTDeclareStatement extends Statement {

    private final String typeName;
    private final String referenceName;
    private final List<Expression> parameters;

    public UDTDeclareStatement(String typeName, String referenceName, List<Expression> parameters) {
        this.typeName = typeName;
        this.referenceName = referenceName;
        this.parameters = parameters;
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

    public List<Expression> getParameters() {
        return parameters;
    }
}
