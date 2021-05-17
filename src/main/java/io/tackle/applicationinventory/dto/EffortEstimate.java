package io.tackle.applicationinventory.dto;

import java.util.Locale;

public enum EffortEstimate {
    SMALL(1),
    MEDIUM(2),
    LARGE(4),
    XLARGE(8);

    private int effort;

    EffortEstimate(int i) {
        this.effort = i;
    }

    public int getEffort() {
        return effort;
    }

    public static EffortEstimate getEnum(String value) {
        return valueOf(value.toUpperCase(Locale.ROOT));
    }
}
