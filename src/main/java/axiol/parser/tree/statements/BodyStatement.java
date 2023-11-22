package axiol.parser.tree.statements;

import axiol.parser.tree.Statement;
import axiol.parser.util.error.Position;

import java.util.List;

public class BodyStatement extends Statement {

    private final Position start, end;
    private final List<Statement> statements;

    public BodyStatement(Position start, Position end, List<Statement> statements) {
        this.start = start;
        this.end = end;
        this.statements = statements;
    }

    public Position getStart() {
        return start;
    }

    public Position getEnd() {
        return end;
    }

    public List<Statement> getStatements() {
        return statements;
    }
}
