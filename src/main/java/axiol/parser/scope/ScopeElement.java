package axiol.parser.scope;

public interface ScopeElement {

    public void push(String name);
    public void pop();

    public void clear();

}
