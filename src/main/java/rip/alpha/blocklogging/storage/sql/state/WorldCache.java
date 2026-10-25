package rip.alpha.blocklogging.storage.sql.state;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.storage.sql.SqlStore;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public final class WorldCache {

    private final Map<String, Integer> ids = new ConcurrentHashMap<>();

    private final SqlStore store;

    @Nullable
    public Integer find(Connection connection, String world) throws SQLException {
        Integer cached = this.ids.get(world);

        if (cached != null)
            return cached;

        Integer found = WorldIds.find(connection, this.store, world);

        if (found != null)
            this.ids.put(world, found);

        return found;
    }
}
