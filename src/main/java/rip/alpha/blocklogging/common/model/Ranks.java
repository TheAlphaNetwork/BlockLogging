package rip.alpha.blocklogging.common.model;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public record Ranks(Map<UUID, RankSnapshot> snapshots, RankSnapshot fallback) {

    public RankSnapshot of(@Nullable UUID uuid) {
        return uuid == null ? this.fallback : this.snapshots.getOrDefault(uuid, this.fallback);
    }

    public int weight(@Nullable UUID uuid) {
        return of(uuid).weight();
    }
}
