package axiol.parser.util;

import axiol.parser.tree.RootNode;
import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;
import axiol.types.Type;

import java.io.File;

public abstract class Parser {

    public abstract RootNode parseFile(final File file) throws Throwable;
    public abstract RootNode parseSource(final File folder, String path, final String content);

    public abstract Statement parseStatement();
    public abstract Expression parseExpression(Type type);

}
