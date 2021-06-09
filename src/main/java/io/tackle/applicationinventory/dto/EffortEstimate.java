package io.tackle.applicationinventory.dto;

import java.util.Arrays;
import java.util.Locale;

public enum EffortEstimate {
    SMALL(1),
    MEDIUM(2),
    LARGE(4),
    EXTRA_LARGE(8);

    private int effort;

    EffortEstimate(int i) {
        this.effort = i;
    }

    public int getEffort() {
        return effort;
    }

    public static EffortEstimate getEnum(String value) {
        return valueOf(transformValue(value));
    }

    public static boolean isExists(String value) {
        return Arrays.stream(EffortEstimate.values()).anyMatch(a -> a.name().equals(transformValue(value)));
    }

    private static String transformValue(String value) {
        return value.replace(" ", "").toUpperCase(Locale.ROOT);
    }
}
