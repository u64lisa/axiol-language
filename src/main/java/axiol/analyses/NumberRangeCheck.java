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
        if (value instanceof I128 i128) {

        }
        if (value instanceof U128 u128) {

        }

        return value.longValue() >= min.longValue() && value.longValue() <= max.longValue();
    }
}
