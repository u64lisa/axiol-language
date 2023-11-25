package axiol.parser.tree.statements.control;

import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;

import java.util.List;

public class ReturnStatement extends Statement {

    private final Expression value;

    public ReturnStatement(Expression value) {
        this.value = value;
    }

    @Override
    public List<Statement> childStatements() {
        return List.of(value);
    }

    public Expression getValue() {
        return value;
    }
}
