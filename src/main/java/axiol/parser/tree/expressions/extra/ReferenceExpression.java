package axiol.parser.tree.expressions.extra;

import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;

import java.util.List;

public class ReferenceExpression extends Expression {

    private final Expression toReference;

    public ReferenceExpression(Expression toReference) {
        this.toReference = toReference;
    }

    @Override
    public List<Statement> childStatements() {
        return List.of(toReference);
    }

    public Expression getToReference() {
        return toReference;
    }
}
