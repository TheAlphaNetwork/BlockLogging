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

    private boolean selection;

    @Nullable
    private String target;

    public long seconds() {
        return TIMES[this.time];
    }

    public int blocks() {
        return RADII[this.radius];
    }

    public String radiusLabel() {
        return this.selection ? "WorldEdit selection" : this.blocks() + " blocks";
    }

    public void pickRadius(int index) {
        this.radius = index;
        this.selection = false;
    }
}
