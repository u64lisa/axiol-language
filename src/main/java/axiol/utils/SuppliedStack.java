package axiol.utils;

import axiol.utils.Stack;

import java.util.LinkedList;
import java.util.function.Supplier;

public class SuppliedStack<T> extends Stack<T> {
    private final LinkedList<T> elements = new LinkedList<>();
    private final Supplier<T> supplier;

    public SuppliedStack(Supplier<T> supplier) {
        this.supplier = supplier;
        this.elements.add(supplier.get());
    }

    public void clear() {
        elements.clear();
    }

    public void push() {
       this.elements.addLast(supplier.get());
    }

    @Override
    public void pop() {
        elements.removeLast();
    }

    @Override
    public T getLast() {
        return elements.getLast();
    }

    @Override
    public void push(T t) {
        elements.addLast(t);
    }

    public LinkedList<T> getElements() {
        return elements;
    }
}