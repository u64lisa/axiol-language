package axiol.parser.tree.statements.oop;

import axiol.parser.statement.Accessibility;
import axiol.parser.statement.Parameter;
import axiol.parser.tree.Statement;
import axiol.parser.tree.statements.BodyStatement;

import java.util.List;

public class ConstructStatement extends Statement {

    private final Accessibility[] accessibility;
    private final List<Parameter> parameters;
    private final BodyStatement bodyStatement;

    public ConstructStatement(Accessibility[] accessibility, List<Parameter> parameters, BodyStatement bodyStatement) {
        this.accessibility = accessibility;
        this.parameters = parameters;
        this.bodyStatement = bodyStatement;
    }

    @Override
    public List<Statement> childStatements() {
        return List.of(bodyStatement);
    }

    public Accessibility[] getAccessibility() {
        return accessibility;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public BodyStatement getBodyStatement() {
        return bodyStatement;
    }
}
