package rip.alpha.blocklogging.rollback.state;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public final class RollbackDraft {

    public static final long[] TIMES = {900L, 3_600L, 21_600L, 86_400L, 259_200L, 604_800L, 1_209_600L};

    public static final int[] RADII = {5, 10, 25, 50, 100};

    private int time = 1;
    private int radius = 1;
    private int scope;

    @Nullable
    private String target;

    public long seconds() {
        return TIMES[this.time];
    }

    public int blocks() {
        return RADII[this.radius];
    }

    public void nextTime() {
        this.time = (this.time + 1) % TIMES.length;
    }

    public void nextRadius() {
        this.radius = (this.radius + 1) % RADII.length;
    }
}
