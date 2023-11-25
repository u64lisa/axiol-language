package axiol.parser.tree.statements.control;

import axiol.parser.tree.Statement;

import java.util.ArrayList;
import java.util.List;

public class UnreachableStatement extends Statement {
    @Override
    public List<Statement> childStatements() {
        return new ArrayList<>();
    }
}
