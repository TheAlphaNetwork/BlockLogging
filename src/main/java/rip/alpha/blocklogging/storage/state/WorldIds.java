package rip.alpha.blocklogging.storage.state;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.storage.StorageService;
import rip.alpha.blocklogging.storage.schema.Tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public final class WorldIds {

    private final Map<String, Integer> ids = new ConcurrentHashMap<>();

    private final StorageService storage;

    public int resolve(Connection connection, String world) throws SQLException {
        Integer cached = this.ids.get(world);

        if (cached != null)
            return cached;

        Integer id = find(connection, this.storage, world);

        if (id == null) {
            insert(connection, world);
            id = Objects.requireNonNull(find(connection, this.storage, world));
        }

        this.ids.put(world, id);
        return id;
    }

    public void clear() {
        this.ids.clear();
    }

    @Nullable
    public static Integer find(Connection connection, StorageService storage, String world) throws SQLException {
        String sql = "SELECT id FROM %s WHERE name = ? ORDER BY id LIMIT 1".formatted(storage.table(Tables.WORLD));

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, world);

            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getInt(1) : null;
            }
        }
    }

    private void insert(Connection connection, String world) throws SQLException {
        String sql = "INSERT INTO %s (name) VALUES (?)".formatted(this.storage.table(Tables.WORLD));

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, world);
            statement.executeUpdate();
        }
    }
}
