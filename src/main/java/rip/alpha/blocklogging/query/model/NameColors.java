package rip.alpha.blocklogging.query.model;

import rip.alpha.blocklogging.common.hook.RankCache;

import java.util.Map;
import java.util.UUID;

public record NameColors(Map<UUID, String> colors) {

    public String of(LookupRow row) {
        return row.uuid() == null ? RankCache.SYSTEM : this.colors.getOrDefault(row.uuid(), RankCache.PLAYER);
    }
}
