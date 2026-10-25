package rip.alpha.blocklogging.inspect.state;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class Inspectors {

    private static final long THROTTLE_MILLIS = 200L;

    private final Set<UUID> inspecting = new HashSet<>();

    private final Map<UUID, Long> lastUse = new HashMap<>();

    public boolean toggle(UUID player) {

        if (this.inspecting.remove(player))
            return false;

        this.inspecting.add(player);

        return true;
    }

    public boolean isInspecting(UUID player) {
        return this.inspecting.contains(player);
    }

    public boolean tryUse(UUID player) {
        long now = System.currentTimeMillis();

        Long last = this.lastUse.get(player);

        if (last != null && now - last < THROTTLE_MILLIS)
            return false;

        this.lastUse.put(player, now);

        return true;
    }

    public void remove(UUID player) {
        this.inspecting.remove(player);
        this.lastUse.remove(player);
    }
}
