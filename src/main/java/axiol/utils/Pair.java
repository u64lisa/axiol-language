package axiol.utils;

public class Pair<A, B> {

    private A first;
    private B last;

    public Pair(A first, B last) {
        this.first = first;
        this.last = last;
    }

    public A getFirst() {
        return first;
    }

    public B getLast() {
        return last;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public void setLast(B last) {
        this.last = last;
    }
}
