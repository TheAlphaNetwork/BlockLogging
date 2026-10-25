package rip.alpha.blocklogging.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public final class LoggingConfig {

    private boolean blockBreak = true;
    private boolean blockPlace = true;
    private boolean explosions = true;
    private boolean fire = true;
    private boolean liquidFlow = true;
    private boolean buckets = true;
    private boolean pistons = true;
    private boolean entityChanges = true;
    private boolean leavesDecay = true;
    private boolean treeGrowth = true;
    private boolean hangings = true;
    private boolean containers = true;
    private boolean hopperTransactions = false;
    private boolean itemDrops = true;
    private boolean itemPickups = true;
    private boolean deathDrops = true;
    private boolean signText = true;
    private boolean entityKills = true;
    private boolean clicks = true;
    private boolean chat = true;
    private boolean commands = true;
    private boolean sessions = true;

    private List<String> disabledWorlds = new ArrayList<>();

    public boolean isDisabled(String world) {
        return this.disabledWorlds.contains(world);
    }
}
