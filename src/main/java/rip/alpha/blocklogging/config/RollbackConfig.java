package rip.alpha.blocklogging.config;

import lombok.Data;

@Data
public final class RollbackConfig {

    private int defaultRadius = 10;
    private int maxRadius = 100;
    private int millisPerTick = 15;

    private boolean requireTime = true;

    private int purgeMinimumDaysInGame = 30;
    private int purgeMinimumDaysConsole = 1;
}
