package axiol.parser.tree.statements.control;

import axiol.parser.tree.Statement;
import axiol.parser.tree.statements.BodyStatement;

public class LoopStatement extends Statement {

    private final BodyStatement bodyStatement;

    public LoopStatement(BodyStatement bodyStatement) {
        this.bodyStatement = bodyStatement;
    }

    public BodyStatement getBodyStatement() {
        return bodyStatement;
    }
}
