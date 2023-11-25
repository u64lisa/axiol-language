package axiol.parser.tree;

import java.util.ArrayList;
import java.util.List;

public class RootNode extends Statement {

    private final List<Statement> statements = new ArrayList<>();

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public List<Statement> childStatements() {
        return statements;
    }
}
