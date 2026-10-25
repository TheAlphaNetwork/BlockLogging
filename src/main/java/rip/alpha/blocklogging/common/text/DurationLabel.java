package rip.alpha.blocklogging.common.text;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DurationLabel {

    public String of(long millis) {

        if (millis < 1000)
            return Math.max(1, millis) + "ms";

        return "%d.%ds".formatted(millis / 1000, millis % 1000 / 100);
    }
}
