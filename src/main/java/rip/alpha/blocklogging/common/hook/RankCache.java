package rip.alpha.blocklogging.common.hook;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import rip.alpha.blocklogging.common.model.RankSnapshot;
import rip.alpha.blocklogging.common.model.Ranks;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

@UtilityClass
public class RankCache {

    public final String SYSTEM = ChatColor.GRAY.toString();
    public final String PLAYER = ChatColor.WHITE.toString();

    private final long TTL = TimeUnit.MINUTES.toMillis(5);
    private final int MAX_CACHED = 10_000;
    private final Logger LOGGER = Logger.getLogger("BlockLogging");

    private final Map<UUID, RankSnapshot> CACHE = new ConcurrentHashMap<>();
    private final Map<UUID, CompletableFuture<RankSnapshot>> LOADING = new ConcurrentHashMap<>();

    public CompletableFuture<Ranks> ranks(Collection<UUID> uuids) {
        Map<UUID, CompletableFuture<RankSnapshot>> stages = new HashMap<>();

        for (UUID uuid : uuids)
            stages.put(uuid, rank(uuid));

        RankSnapshot fallback = fallback(System.currentTimeMillis());

        return CompletableFuture.allOf(stages.values().toArray(CompletableFuture[]::new)).thenApply(_ -> {
            Map<UUID, RankSnapshot> snapshots = new HashMap<>();

            stages.forEach((uuid, stage) -> snapshots.put(uuid, stage.join()));
            return new Ranks(Map.copyOf(snapshots), fallback);
        });
    }

    public CompletableFuture<RankSnapshot> rank(UUID uuid) {
        RankSnapshot cached = CACHE.get(uuid);

        if (cached != null && cached.expires() > System.currentTimeMillis())
            return CompletableFuture.completedFuture(cached);

        CompletableFuture<RankSnapshot> created = new CompletableFuture<>();
        CompletableFuture<RankSnapshot> running = LOADING.putIfAbsent(uuid, created);

        if (running != null)
            return running;

        long expires = System.currentTimeMillis() + TTL;

        load(uuid, expires).whenComplete((found, error) -> {
            RankSnapshot snapshot = found == null ? fallback(expires) : found;

            if (error != null && !(unwrap(error) instanceof TimeoutException))
                LOGGER.log(Level.WARNING, "Could not load the rank of " + uuid, unwrap(error));

            remember(uuid, snapshot);
            LOADING.remove(uuid, created);
            created.complete(snapshot);
        });

        return created;
    }

    private CompletableFuture<RankSnapshot> load(UUID uuid, long expires) {
        if (!Bukkit.getPluginManager().isPluginEnabled("Core"))
            return CompletableFuture.completedFuture(null);

        return CompletableFuture.completedFuture(uuid).thenCompose(id -> CoreRanks.load(id, expires));
    }

    private void remember(UUID uuid, RankSnapshot snapshot) {
        if (CACHE.size() >= MAX_CACHED) {
            long now = System.currentTimeMillis();

            CACHE.values().removeIf(cached -> cached.expires() <= now);
        }

        CACHE.put(uuid, snapshot);
    }

    private RankSnapshot fallback(long expires) {
        return new RankSnapshot(PLAYER, 0, expires);
    }

    private Throwable unwrap(Throwable error) {
        return error instanceof CompletionException && error.getCause() != null ? error.getCause() : error;
    }
}
