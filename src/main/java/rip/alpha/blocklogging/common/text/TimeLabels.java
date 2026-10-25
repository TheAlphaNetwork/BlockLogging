package rip.alpha.blocklogging.common.text;

import lombok.experimental.UtilityClass;
import rip.alpha.libraries.time.TimeFormat;

import java.util.concurrent.TimeUnit;

@UtilityClass
public class TimeLabels {

    public String of(long seconds) {
        return TimeFormat.detailed(TimeUnit.SECONDS.toMillis(seconds));
    }
}
