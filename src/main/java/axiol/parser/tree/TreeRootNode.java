package axiol.parser.tree;

import java.util.ArrayList;
import java.util.List;

public class TreeRootNode extends Statement {

    private final List<Statement> statements = new ArrayList<>();

    public List<Statement> getStatements() {
        return statements;
    }

}
