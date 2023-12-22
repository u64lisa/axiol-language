package axiol.types.custom;

public class I128 extends CustomNumber<I128> {

    private static final I128 ZERO = new I128(0,0);

    private long positive;
    private long negative;

    public I128(long positive, long negative) {
        this.positive = positive;
        this.negative = negative;
    }

    public I128(String value) {
        // todo parse string
    }

    public I128(long fromLong) {
        // todo long
    }

    @Override
    public I128 add(I128 other) {
        long combPositive = this.positive + other.positive;
        long combNegative = this.negative + other.negative;

        if (combPositive < 0 || combNegative < 0) {
            throw new ArithmeticException("Overflow in addition");
        }

        return new I128(combPositive, combNegative);
    }

    @Override
    public I128 subtract(I128 other) {
        long combPositive = this.positive - other.positive;
        long combNegative = this.negative - other.negative;

        if (combPositive < 0 || combNegative < 0) {
            throw new ArithmeticException("Overflow in subtraction");
        }

        return new I128(combPositive, combNegative);
    }

    @Override
    public I128 multiply(I128 other) {
        return null;
    }

    @Override
    public I128 divide(I128 other) {
        return null;
    }

    @Override
    public I128 shiftLeft(I128 other) {
        int shiftAmount = (int) (other.longValue() & 0x7F); // Only consider lower 7 bits for shift amount
        return shiftLeft(shiftAmount);
    }

    @Override
    public I128 shiftLeft(int other) {
        if (other >= 128) {
            return I128.ZERO; // Shifting by 128 or more bits results in zero
        }

        long shiftedPositive;
        long shiftedNegative;

        if (other >= 64) {
            shiftedPositive = (negative >> (other - 64)) | (positive << (128 - other));
            shiftedNegative = positive >> (other - 64);
        } else {
            shiftedPositive = (positive << other) | (negative >> (64 - other));
            shiftedNegative = negative << other;
        }

        return new I128(shiftedPositive, shiftedNegative);
    }

    @Override
    public I128 shiftRight(I128 other) {
        int shiftAmount = (int) (other.longValue() & 0x7F); // Only consider lower 7 bits for shift amount
        return shiftRight(shiftAmount);
    }

    @Override
    public I128 shiftRight(int other) {
        if (other >= 128) {
            return I128.ZERO; // Shifting by 128 or more bits results in zero
        }

        long shiftedPositive;
        long shiftedNegative;

        if (other >= 64) {
            shiftedPositive = positive >> (other - 64);
            shiftedNegative = (positive << (128 - other)) | (negative >> other);
        } else {
            shiftedPositive = positive >> other;
            shiftedNegative = (negative << (64 - other)) | (positive >> (64 - other));
        }

        return new I128(shiftedPositive, shiftedNegative);
    }

    @Override
    public I128 xor(I128 other) {
        return new I128(this.positive ^ other.positive,
                this.negative^ other.negative);
    }

    @Override
    public I128 or(I128 other) {
        return new I128(this.positive | other.positive,
                this.negative | other.negative);
    }

    @Override
    public I128 and(I128 other) {
        return new I128(this.positive & other.positive,
                this.negative & other.negative);
    }

    @Override
    public I128 remainder(I128 other) {
        if (other.isZero())
            throw new ArithmeticException("Division by zero");

        I128 quotient = this.divide(other);
        I128 product = other.multiply(quotient);

        return this.subtract(product);
    }

    @Override
    public I128 negate() {
        return new I128(negative, positive);
    }

    @Override
    public boolean isZero() {
        return this.positive == 0 && this.negative == 0;
    }

    @Override
    public boolean biggerThan(I128 other) {
        return compareTo(other) > 0;
    }

    @Override
    public boolean smallerThan(I128 other) {
        return compareTo(other) < 0;
    }

    @Override
    public boolean equals(I128 customNumber) {
        return compareTo(customNumber) == 0;
    }

    @Override
    public String toHexString() {
        return "%d%016X".formatted(positive, negative);
    }

    @Override
    public int compareTo(I128 o) {
        int compared = Long.compare(this.positive, o.positive);
        if (compared != 0) {
            return compared;
        }
        return Long.compareUnsigned(this.negative, o.negative);
    }

    @Override
    public int intValue() {
        return (int) ((positive << 32) | (negative & 0xFFFFFFFF));
    }

    @Override
    public long longValue() {
        return (positive << 32) | (negative & 0xFFFFFFFFFFFFFFFFL);
    }

    @Override
    public float floatValue() {
        // Convert the combined low 64 bits to a float
        return Float.intBitsToFloat((int) ((positive << 32) | (negative >>> 32)));
    }

    @Override
    public double doubleValue() {
        // Convert the combined 128 bits to a double
        long combined = (positive << 32) | (negative & 0xFFFFFFFFFFFFFFFFL);
        return Double.longBitsToDouble(combined);
    }
}
