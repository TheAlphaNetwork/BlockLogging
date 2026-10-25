package rip.alpha.blocklogging.common.text;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AgeLabel {

    private final long[] UNITS = {86_400L * 365, 86_400L * 7, 86_400L, 3_600L, 60L};

    private final char[] SUFFIXES = {'y', 'w', 'd', 'h', 'm'};

    public String of(long seconds) {

        for (int index = 0; index < UNITS.length; index++) {

            if (seconds >= UNITS[index]) {
                long tenths = seconds * 10 / UNITS[index];

                return "%d.%d%s".formatted(tenths / 10, tenths % 10, SUFFIXES[index]);
            }
        }

        return Math.max(0L, seconds) + "s";
    }
}
