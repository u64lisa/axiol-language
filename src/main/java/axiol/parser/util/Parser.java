package axiol.parser.util;

import axiol.parser.tree.TreeRootNode;
import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;

import java.io.File;

public abstract class Parser {

    public abstract TreeRootNode parseFile(final File file) throws Throwable;
    public abstract TreeRootNode parseSource(final String path, final String content);

    public abstract Statement parseStatement();
    public abstract Expression parseExpression();

}
