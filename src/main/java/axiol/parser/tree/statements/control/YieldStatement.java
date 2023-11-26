package axiol.parser.tree.statements.control;

import axiol.parser.tree.Expression;
import axiol.parser.util.error.TokenPosition;

public class YieldStatement extends ReturnStatement {

    public YieldStatement(Expression value, TokenPosition position) {
        super(value, position);
    }

}
