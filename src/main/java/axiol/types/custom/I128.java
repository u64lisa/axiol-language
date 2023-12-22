package axiol.types.custom;

import java.math.BigInteger;

@SuppressWarnings("unused")
public class I128 {

    private long high;
    private long low;

    public I128(long high, long low) {
        this.high = high;
        this.low = low;
    }

    public I128() {

    }

    public I128 fromString(String text) {
        assert text != null && !text.isEmpty() : "Invalid string!";
        text = text.replaceFirst("^0+", "");

        BigInteger integer = new BigInteger(text);
        high = integer.shiftRight(64).longValue();
        low = integer.longValue();

        return this;
    }

    public I128 add(I128 other) {
        long newLow = this.low + other.low;
        long carry = (newLow < 0 && other.low < 0) || (newLow >= 0 && other.low >= 0) ? 0 : 1;
        long newHigh = this.high + other.high + carry;
        return new I128(newHigh, newLow);
    }

    public I128 subtract(I128 other) {
        return add(other.negate());
    }

    // todo recode
    @Deprecated
    public I128 multiply(I128 other) {
        long[] currentArray = split(this);
        long[] otherArray = split(other);

        long[] result = new long[4];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                int firstIndex = i + j;
                int secondIndex = i + j + 1;

                long product = currentArray[i] * otherArray[j] + result[firstIndex];
                result[firstIndex] = product & 0xFFFFFFFFL;
                result[secondIndex] += product >>> 32;
            }
        }
        return combine(result);
    }

    // todo recode
    @Deprecated
    public I128 divide(I128 divisor) {
        if (divisor.equal(new I128(0, 0))) {
            throw new ArithmeticException("Division by zero");
        }

        I128 quotient = new I128();
        I128 remainder = new I128();
        I128 absDividend = this.abs();
        I128 absDivisor = divisor.abs();

        while (absDividend.compare(absDivisor) >= 0) {
            absDividend = absDividend.subtract(absDivisor);
            quotient = quotient.add(new I128(0, 1));
        }

        if ((this.high > 0) ^ (divisor.high < 0)) {
            quotient = quotient.negate();
        }
        return quotient;
    }

    public long[] split(I128 value) {
        return new long[]{
                value.high >>> 32, value.high & 0xFFFFFFFL,
                value.low >>> 32, value.low & 0xFFFFFFFL
        };
    }

    public I128 combine(long[] values) {
        return new I128((values[0] << 32) | values[1], (values[2] << 32) | values[3]);
    }

    public I128 abs() {
        return this.compare(new I128(0, 0)) >= 0 ? this : this.negate();
    }

    public I128 shiftLeft(int pos) {
        if (pos >= 64) {
            return new I128(low << (pos - 64), 0);
        } else {
            long newHigh = (high << pos) | (low >>> (64 - pos));
            long newLow = low << pos;
            return new I128(newHigh, newLow);
        }
    }

    public I128 shiftRight(int pos) {
        if (pos >= 64) {
            return new I128(0, high >>> (pos - 64));
        } else {
            long newLow = (low >>> pos) | (high << (64 - 4));
            long newHigh = high >> pos;
            return new I128(newHigh, newLow);
        }
    }

    public I128 bitwiseAnd(I128 other) {
        return new I128(this.high & other.high, this.low & other.low);
    }

    public I128 bitwiseOr(I128 other) {
        return new I128(this.high | other.high, this.low | other.low);
    }

    public I128 bitwiseXOr(I128 other) {
        return new I128(this.high ^ other.high, this.low ^ other.low);
    }

    public I128 negate() {
        return new I128(~this.high, ~this.low)
                .add(new I128(0, 1));
    }

    public int compare(I128 other) {
        if (this.high < other.high) return -1;
        if (this.high > other.high) return 1;
        return Long.compareUnsigned(this.low, other.low);
    }

    public I128 modulus(I128 divisor) {
        if (divisor.equal(new I128(0, 0))) {
            throw new ArithmeticException("Division by zero");
        }

        I128 result = this;
        while (result.compare(divisor) >= 0) {
            result = result.subtract(divisor);
        }

        return result;
    }

    public long toLong() {
        if (high != 0 && high != -1) {
            throw new ArithmeticException("Cannot cast I128 to long either to big or small");
        }

        return low;
    }

    public boolean equal(I128 other) {
        return this.compare(other) == 0;
    }

    public I128 cast(long value) {
        return new I128((value < 0) ? -1 : 0,value);
    }

    @Override
    public String toString() {
        return "%d%016X".formatted(high, low);
    }

}
