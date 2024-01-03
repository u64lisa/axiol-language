package axiol.parser.util.scope;

public interface ScopeElement {

    public void push(String name);
    public void pop();

    public void clear();

}
