package axiol.types.custom;

public class U128 extends CustomNumber<U128> {

    private long positive;
    private long secondPositive;

    @Override
    public U128 add(U128 other) {
        return null;
    }

    @Override
    public U128 subtract(U128 other) {
        return null;
    }

    @Override
    public U128 multiply(U128 other) {
        return null;
    }

    @Override
    public U128 divide(U128 other) {
        return null;
    }

    @Override
    public U128 shiftLeft(U128 other) {
        return null;
    }

    @Override
    public U128 shiftRight(U128 other) {
        return null;
    }

    @Override
    public U128 xor(U128 other) {
        return null;
    }

    @Override
    public U128 or(U128 other) {
        return null;
    }

    @Override
    public U128 and(U128 other) {
        return null;
    }

    @Override
    public U128 remainder(U128 other) {
        return null;
    }

    @Override
    public boolean biggerThan(U128 other) {
        return false;
    }

    @Override
    public boolean smallerThan(U128 other) {
        return false;
    }

    @Override
    public boolean equals(U128 customNumber) {
        return false;
    }

    @Override
    public String toHexString() {
        return "%d%016X".formatted(positive, secondPositive);
    }

    @Override
    public int compareTo(U128 o) {
        return 0;
    }

    @Override
    public int intValue() {
        return 0;
    }

    @Override
    public long longValue() {
        return 0;
    }

    @Override
    public float floatValue() {
        return 0;
    }

    @Override
    public double doubleValue() {
        return 0;
    }
}
