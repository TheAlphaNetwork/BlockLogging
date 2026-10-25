package rip.alpha.blocklogging.rollback.state;

import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.rollback.model.PendingPreview;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class Previews {

    private final Map<UUID, PendingPreview> previews = new ConcurrentHashMap<>();

    @Nullable
    public PendingPreview put(UUID player, PendingPreview preview) {
        return this.previews.put(player, preview);
    }

    @Nullable
    public PendingPreview remove(UUID player) {
        return this.previews.remove(player);
    }
}
