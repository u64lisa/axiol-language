package axiol.parser.tree.statements;

import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class LinkedNoticeStatement extends Statement {

    private final String linkedName;

    public LinkedNoticeStatement(String linkedName) {
        this.linkedName = linkedName;
    }

    public String getLinkedName() {
        return linkedName;
    }

    @Override
    public NodeType type() {
        return NodeType.LINKED_STATEMENT;
    }


    @Override
    public List<Statement> childStatements() {
        return new ArrayList<>();
    }
}
