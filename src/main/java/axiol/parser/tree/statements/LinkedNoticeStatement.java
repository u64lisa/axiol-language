package axiol.parser.tree.statements;

import axiol.parser.tree.Statement;

import java.util.Stack;

public class LinkedNoticeStatement extends Statement {

    private final String linkedName;

    public LinkedNoticeStatement(String linkedName) {
        this.linkedName = linkedName;
    }

    public String getLinkedName() {
        return linkedName;
    }
}
