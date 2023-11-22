package axiol.parser.tree.statements.control;

import axiol.parser.tree.Expression;

public class YieldStatement extends ReturnStatement {

    public YieldStatement(Expression value) {
        super(value);
    }

}
