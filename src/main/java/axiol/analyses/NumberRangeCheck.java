package axiol.analyses;

public class NumberRangeCheck {
    private final long max, min;

    public NumberRangeCheck(long max, long min) {
        this.max = max;
        this.min = min;
    }

    public boolean isValid(long value) {
        return value >= min && value <= max;
    }
}
