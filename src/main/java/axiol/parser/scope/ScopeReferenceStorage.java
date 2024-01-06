package axiol.parser.scope;

public abstract class ScopeReferenceStorage {
    public abstract void clear();

    public abstract void pushBlock();

    public abstract void popBlock();
}
