package axiol.types.custom;

@SuppressWarnings("unused")
public class I128 extends CustomNumber<I128> {
    /**
     * The constant SIZE.
     */
    public static final int SIZE = 128;
    /**
     * The constant BYTES.
     */
    public static final int BYTES = 16;

    /**
     * The constant MIN_VALUE.
     */
    public static final I128 MIN_VALUE = new I128(0x0000_0000_0000_0000L, 0x0000_0000_0000_0000L);
    /**
     * The constant MAX_VALUE.
     */
    public static final I128 MAX_VALUE = new I128(0x8000_0000_0000_0000L, 0x0000_0000_0000_0000L);
    /**
     * The constant ZERO.
     */
    public static final I128 ZERO = new I128(0L, 0L);
    /**
     * The constant ONE.
     */
    public static final I128 ONE = new I128(0L, 1L);

    private static final double TWO_TO_32 = Math.pow(2, 32);

    private static final long INT_MASK = (1L << 32) - 1L;

    private final int high;
    private final int midHigh;
    private final int midLow;
    private final int low;

    /**
     * Instantiates a new U 128.
     *
     * @param value the value
     */
    public I128(long value) {
        this(0L, value);
    }

    /**
     * Instantiates a new U 128.
     *
     * @param value the value
     */
    public I128(int value) {
        this(value & 0xFFFF_FFFFL);
    }

    /**
     * Instantiates a new U 128.
     *
     * @param value the value
     */
    public I128(short value) {
        this(value & 0xFFFF);
    }

    /**
     * Instantiates a new U 128.
     *
     * @param high the high
     * @param low  the low
     */
    public I128(long high, long low) {
        this((int) (high >>> 32), (int) high, (int) (low >>> 32), (int) low);
    }

    /**
     * Instantiates a new U 128.
     *
     * @param high    the high
     * @param midHigh the mid high
     * @param midLow  the mid low
     * @param low     the low
     */
    public I128(int high, int midHigh, int midLow, int low) {
        this.high = high;
        this.midHigh = midHigh;
        this.midLow = midLow;
        this.low = low;
    }

    /**
     * Instantiates a new U 128.
     *
     * @param text the text
     */
    public I128(String text) {
        int len = text.length();
        if (len > 0) {
            int i = 0;
            char ch = text.charAt(0);
            if (ch == '+') {
                i += 1; // skip first char
            }
            if (i >= len) {
                throw new NumberFormatException(text);
            }
            // No real effort to catch overflow here
            I128 result = I128.ZERO;
            while (i < len) {
                int digit = Character.digit(text.charAt(i++), 10);
                if (digit < 0) {
                    throw new NumberFormatException(text);
                }
                result = result.multiply(new I128(0, 10L))
                        .add(new I128(0, digit));
            }

            this.high = result.high;
            this.midHigh = result.midHigh;
            this.midLow = result.midLow;
            this.low = result.low;
        } else {
            throw new NumberFormatException(text);
        }
    }

    @Override
    public I128 setFromHexString(String hexValue) {
        if (hexValue.startsWith("0x") || hexValue.startsWith("0X")) {
            hexValue = hexValue.substring(2);
        }

        int length = hexValue.length();
        int mid = Math.min(16, length);

        long high = Long.parseLong(hexValue.substring(0, mid), 16);
        long low = (length > 16) ? Long.parseLong(hexValue.substring(mid), 16) : 0L;

        return new I128(high, low);
    }

    @Override
    public I128 setFromDecimalString(String decimalValue) {
        I128 result = I128.ZERO;

        int length = decimalValue.length();
        int endIndex = Math.min(length, 18);

        // Process the first 18 digits (if available)
        for (int i = 0; i < endIndex; i++) {
            char digitChar = decimalValue.charAt(i);
            int digit = Character.digit(digitChar, 10);
            result = result.multiply(new I128(10)).add(new I128(digit));
        }

        // Process the remaining digits (if any)
        for (int i = endIndex; i < length; i++) {
            char digitChar = decimalValue.charAt(i);
            int digit = Character.digit(digitChar, 10);
            result = result.multiply(new I128(10)).add(new I128(digit));
        }

        return result;
    }

    @Override
    public I128 add(I128 other) {
        long partial0 = (this.low & INT_MASK) + (other.low & INT_MASK);
        long partial1 =
                (this.midLow & INT_MASK) + (other.midLow & INT_MASK) + (partial0 >>> 32);
        long partial2 =
                (this.midHigh & INT_MASK) + (other.midHigh & INT_MASK) + (partial1 >>> 32);
        long partial3 = (this.high & INT_MASK) + (other.high & INT_MASK) + (partial2 >>> 32);
        return new I128((int) partial3, (int) partial2, (int) partial1, (int) partial0);
    }

    @Override
    public I128 subtract(I128 other) {
        long partial0 = (this.low & INT_MASK) - (other.low & INT_MASK);
        long partial1 =
                (this.midLow & INT_MASK) - (other.midLow & INT_MASK) + (partial0 >> 32);
        long partial2 =
                (this.midHigh & INT_MASK) - (other.midHigh & INT_MASK) + (partial1 >> 32);
        long partial3 = (this.high & INT_MASK) - (other.high & INT_MASK) + (partial2 >> 32);
        return new I128((int) partial3, (int) partial2, (int) partial1, (int) partial0);
    }

    @Override
    public I128 multiply(I128 multiplicand) {
        long thisLow = (this.low & INT_MASK);
        long thisMidLow = (this.midLow & INT_MASK);
        long thisMidHigh = (this.midHigh & INT_MASK);
        long thisHigh = (this.high & INT_MASK);

        long multiplicandLow = (multiplicand.low & INT_MASK);
        long partial00 = thisLow * multiplicandLow;
        long partial01 = thisMidLow * multiplicandLow + (partial00 >>> 32);
        long partial02 = thisMidHigh * multiplicandLow + (partial01 >>> 32);
        long partial03 = thisHigh * multiplicandLow + (partial02 >>> 32);

        long multiplicandMidLow = (multiplicand.midLow & INT_MASK);
        long partial10 = thisLow * multiplicandMidLow;
        long partial11 = thisMidLow * multiplicandMidLow + (partial10 >>> 32);
        long partial12 = thisMidHigh * multiplicandMidLow + (partial11 >>> 32);

        long multiplicandMidHigh = (multiplicand.midHigh & INT_MASK);
        long partial20 = thisLow * multiplicandMidHigh;
        long partial21 = thisMidLow * multiplicandMidHigh + (partial20 >>> 32);

        long partial30 = thisLow * (multiplicand.high & INT_MASK);

        long ll = (partial00 & INT_MASK);
        long ml = (partial10 & INT_MASK) + (partial01 & INT_MASK);
        long mh = (partial20 & INT_MASK) + (partial11 & INT_MASK) + (partial02 & INT_MASK) + (ml >>> 32);
        long hh = (partial30 & INT_MASK) + (partial21 & INT_MASK) + (partial12 & INT_MASK) + (partial03 & INT_MASK) + (mh >>> 32);

        return new I128((int) hh, (int) mh, (int) ml, (int) ll);
    }

    @Override
    public I128 divide(I128 divisor) {
        if (divisor.isZero()) {
            throw new IllegalArgumentException("Cannot divide by zero");
        }

        // Some special cases
        if (this.isZero()) {
            return ZERO;
        }

        int comparison = this.compareTo(divisor);

        if (comparison < 0) {
            return ZERO;
        }

        if (comparison == 0) {
            return ONE;
        }

        I128 result = I128.ZERO;
        I128 remainder = I128.ZERO;
        I128 numerator = this;

        for (int i = 0; i < SIZE; ++i) {
            remainder = remainder.shiftLeft(1);
            result = result.shiftLeft(1);

            if (numerator.high < 0) {
                remainder = remainder.or(I128.ONE);
            }

            numerator = numerator.shiftLeft(1);

            if (remainder.compareTo(divisor) >= 0) {
                remainder = remainder.subtract(divisor);
                result = result.or(I128.ONE);
            }
        }

        return result;
    }

    @Override
    public I128 remainder(I128 divisor) {
        if (divisor.isZero()) {
            throw new IllegalArgumentException("Can't divide by zero");
        }
        // Some special cases
        if (this.isZero()) {
            return ZERO;
        }
        int cmp = this.compareTo(divisor);
        if (cmp < 0) {
            return this;
        }
        if (cmp == 0) {
            return ZERO;
        }
        I128 result = I128.ZERO;
        I128 current = this;
        for (int i = 0; i < SIZE; ++i) {
            result = result.shiftLeft(1);
            if (current.high < 0) {
                result = result.or(I128.ONE);
            }
            current = current.shiftLeft(1);
            if (result.compareTo(divisor) >= 0) {
                result = result.subtract(divisor);
            }
        }
        return result;
    }

    @Override
    public I128 pow(int exp) {
        if (exp < 0) {
            throw new IllegalArgumentException("exp must be >= 0");
        }

        I128 result = I128.ONE;
        I128 base = this;

        while (exp != 0) {
            if ((exp & 1) != 0) {
                result = result.multiply(base);
            }

            base = base.multiply(base);
            exp >>>= 1;
        }
        return result;
    }

    @Override
    public I128 shiftLeft(int shiftCount) {
        if (shiftCount == 0) {
            return this;
        } else if (shiftCount >= SIZE || shiftCount <= -SIZE) {
            return ZERO; // All bits are gone
        } else if (shiftCount < 0) {
            return shiftRight(-shiftCount); // Negative left shift is right shift
        }

        long highPart = (shiftCount >= Long.SIZE) ? this.getLow() : this.getHigh();
        long lowPart = (shiftCount >= Long.SIZE) ? 0 : this.getLow();
        int remainder = shiftCount % Long.SIZE;

        if (remainder > 0) {
            long carry = lowPart >>> (Long.SIZE - remainder);
            highPart = (highPart << remainder) | carry;
            lowPart <<= remainder;
        }

        return new I128(highPart, lowPart);
    }

    @Override
    public I128 shiftRight(int shiftCount) {
        if (shiftCount == 0) {
            return this;
        } else if (shiftCount >= SIZE || shiftCount <= -SIZE) {
            return ZERO; // All bits are gone
        } else if (shiftCount < 0) {
            return shiftLeft(-shiftCount); // Negative right shift is left shift
        }

        long highPart = (shiftCount >= Long.SIZE) ? 0L : this.getHigh();
        long lowPart = (shiftCount >= Long.SIZE) ? this.getHigh() : this.getLow();
        int remainder = shiftCount % Long.SIZE;

        if (remainder > 0) {
            long carry = highPart << (Long.SIZE - remainder);
            highPart >>>= remainder;
            lowPart = (lowPart >>> remainder) | carry;
        }

        return new I128(highPart, lowPart);
    }

    @Override
    public I128 negate() {
        return new I128(~this.high, ~this.midHigh, ~this.midLow, ~this.low);
    }

    @Override
    public int compareTo(I128 n) {
        // Compare signed values
        if (this.high < n.high) return -1;
        if (this.high > n.high) return 1;

        if (this.midHigh < n.midHigh) return -1;
        if (this.midHigh > n.midHigh) return 1;

        if (this.midLow < n.midLow) return -1;
        if (this.midLow > n.midLow) return 1;

        return Integer.compareUnsigned(this.low, n.low);
    }

    /**
     * Gets high.
     *
     * @return the high
     */
    public long getHigh() {
        return ((this.high & INT_MASK) << 32) | (this.midHigh & INT_MASK);
    }

    /**
     * Gets low.
     *
     * @return the low
     */
    public long getLow() {
        return ((this.midLow & INT_MASK) << 32) | (this.low & INT_MASK);
    }

    @Override
    public I128 or(I128 other) {
        return new I128(
                this.high | other.high,
                this.midHigh | other.midHigh,
                this.midLow | other.midLow,
                this.low | other.low);
    }

    @Override
    public I128 and(I128 other) {
        return new I128(this.high & other.high, this.midHigh & other.midHigh,
                this.midLow & other.midLow, this.low & other.low);
    }

    @Override
    public I128 xor(I128 other) {
        return new I128(
                this.high ^ other.high, this.midHigh ^ other.midHigh,
                this.midLow ^ other.midLow, this.low ^ other.low);
    }

    public boolean isZero() {
        return this.high == 0 && this.midHigh == 0 && this.midLow == 0 && this.low == 0;
    }

    @Override
    public String toHexString() {
        String highHex = Long.toHexString(this.getHigh());
        String lowHex = Long.toHexString(this.getLow());

        highHex = padWithZeros(highHex);
        lowHex = padWithZeros(lowHex);

        return highHex + lowHex;
    }

    private String padWithZeros(String str) {
        int paddingLength = 16 - str.length();
        if (paddingLength > 0) {
            return "0".repeat(paddingLength) +
                    str;
        }
        return str;
    }

    @Override
    public boolean biggerThan(I128 other) {
        return this.compareTo(other) > 0;
    }

    @Override
    public boolean smallerThan(I128 other) {
        return this.compareTo(other) < 0;
    }

    @Override
    public boolean equals(I128 customNumber) {
        return this.high == customNumber.high
                && this.midHigh == customNumber.midHigh
                && this.midLow == customNumber.midLow
                && this.low == customNumber.low;
    }

    @Override
    public int hashCode() {
        return this.high * 31 + this.midHigh * 23 + this.midLow * 13 + this.low;
    }

    @Override
    public String toString() {
        return toString(10);
    }

    @Override
    public String toString(int radix) {
        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
            throw new IllegalArgumentException("Illegal radix: " + radix);
        }

        if (isZero()) {
            return "0";
        }

        StringBuilder result = new StringBuilder();
        I128 number = this;
        I128 base = new I128(radix);

        while (!number.isZero()) {
            I128 digit = number.remainder(base);
            result.append(Character.forDigit(digit.low, radix));
            number = number.divide(base);
        }

        return result.reverse().toString();
    }

    @Override
    public int intValue() {
        return (int) this.getLow();
    }

    @Override
    public long longValue() {
        return (this.getHigh() << 32) | (this.getLow() & 0xFFFFFFFFL);
    }

    @Override
    public float floatValue() {
        return (float) doubleValue();
    }

    @Override
    public double doubleValue() {
        return ((double) this.getHigh() * TWO_TO_32) + (this.getLow() & 0xFFFFFFFFL);
    }
}
