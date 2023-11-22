package axiol.parser.tree.statements.control;

import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;

public class ReturnStatement extends Statement {

    private final Expression value;

    public ReturnStatement(Expression value) {
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }
}
