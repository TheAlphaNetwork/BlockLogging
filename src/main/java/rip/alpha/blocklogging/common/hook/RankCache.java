package rip.alpha.blocklogging.common.hook;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.RankSnapshot;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class RankCache {

    public final String SYSTEM = ChatColor.GRAY.toString();
    public final String PLAYER = ChatColor.WHITE.toString();

    private final long TTL = TimeUnit.MINUTES.toMillis(5);

    private final Map<UUID, RankSnapshot> CACHE = new ConcurrentHashMap<>();

    public String color(@Nullable UUID uuid) {
        return uuid == null ? SYSTEM : of(uuid).color();
    }

    public int weight(UUID uuid) {
        return of(uuid).weight();
    }

    private RankSnapshot of(UUID uuid) {
        long now = System.currentTimeMillis();

        RankSnapshot cached = CACHE.get(uuid);

        if (cached != null && cached.expires() > now)
            return cached;

        RankSnapshot loaded = Bukkit.getPluginManager().isPluginEnabled("Core") ? CoreRanks.snapshot(uuid, now + TTL) : null;
        RankSnapshot snapshot = loaded == null ? new RankSnapshot(PLAYER, 0, now + TTL) : loaded;

        CACHE.put(uuid, snapshot);

        return snapshot;
    }
}
