package axiol.types.custom;

public abstract class CustomNumber<T extends CustomNumber<?>> extends Number implements Comparable<T> {

    public abstract T setFromHexString(String hexValue);
    public abstract T setFromDecimalString(String decimalValue);

    public abstract T add(T other);
    public abstract T subtract(T other);
    public abstract T multiply(T other);
    public abstract T divide(T other);

    public abstract U128 pow(int exp);

    public abstract T shiftLeft(int other);
    public abstract T shiftRight(int other);

    public abstract T xor(T other);
    public abstract T or(T other);
    public abstract T and(T other);

    public abstract T remainder(T other);
    public abstract T negate();

    public abstract boolean isZero();
    public abstract String toHexString();

    public abstract boolean biggerThan(T other);
    public abstract boolean smallerThan(T other);
    public abstract boolean equals(T customNumber);

    public abstract String toString(int radix);
}
