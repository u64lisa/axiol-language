package axiol.parser.util;

import axiol.parser.tree.RootNode;
import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;
import axiol.parser.util.scope.Scope;
import axiol.types.SimpleType;

import java.io.File;

public abstract class Parser {

    public abstract RootNode parseFile(final File file) throws Throwable;
    public abstract RootNode parseSource(final String path, final String content);

    public abstract Statement parseStatement(Scope scope);
    public abstract Expression parseExpression(Scope scope, SimpleType simpleType);

}
