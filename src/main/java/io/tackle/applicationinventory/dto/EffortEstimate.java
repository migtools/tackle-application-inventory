/*
 * Copyright © 2021 Konveyor (https://konveyor.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
