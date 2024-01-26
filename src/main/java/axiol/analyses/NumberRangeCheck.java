package axiol.analyses;

import axiol.types.custom.I128;
import axiol.types.custom.U128;

public class NumberRangeCheck {
    private final Number max, min;

    public NumberRangeCheck(Number max, Number min) {
        this.max = max;
        this.min = min;
    }

    public boolean isValid(Number value) {
        // todo currenty only < > but need >= <=
        if (value instanceof I128 i128) {
            return i128.biggerThan(I128.MIN_VALUE) && i128.smallerThan(I128.MAX_VALUE);
        }
        if (value instanceof U128 u128) {
            return u128.biggerThan(U128.MIN_VALUE) && u128.smallerThan(U128.MAX_VALUE);
        }

        return value.longValue() >= min.longValue() && value.longValue() <= max.longValue();
    }
}
