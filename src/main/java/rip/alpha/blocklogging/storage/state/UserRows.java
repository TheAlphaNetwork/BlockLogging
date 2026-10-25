package rip.alpha.blocklogging.storage.state;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.storage.StorageService;
import rip.alpha.blocklogging.storage.model.UserRow;
import rip.alpha.blocklogging.storage.schema.Tables;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
final class UserRows {

    private static final String BY_UUID = "SELECT id, name FROM %s WHERE uuid = ? ORDER BY id LIMIT 1";
    private static final String BY_NAME = "SELECT id, name FROM %s WHERE uuid IS NULL AND name = ? ORDER BY id LIMIT 1";

    private final StorageService storage;

    @Nullable
    UserRow find(Connection connection, Actor actor) throws SQLException {
        String sql = (actor.isPlayer() ? BY_UUID : BY_NAME).formatted(this.storage.table(Tables.USER));

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, actor.isPlayer() ? String.valueOf(actor.uuid()) : actor.name());

            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? new UserRow(result.getInt(1), result.getString(2)) : null;
            }
        }
    }

    void insert(Connection connection, Actor actor) throws SQLException {
        String sql = "INSERT INTO %s (name, uuid) VALUES (?, ?)".formatted(this.storage.table(Tables.USER));

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, actor.name());
            statement.setString(2, actor.isPlayer() ? String.valueOf(actor.uuid()) : null);
            statement.executeUpdate();
        }
    }

    void rename(Connection connection, int id, String name) throws SQLException {
        String sql = "UPDATE %s SET name = ? WHERE id = ?".formatted(this.storage.table(Tables.USER));

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setInt(2, id);
            statement.executeUpdate();
        }
    }

    void recordName(Connection connection, int id, String name, long time) throws SQLException {
        String sql = "INSERT INTO %s (time, user, world, x, y, z, text) VALUES (?, ?, 0, 0, 0, 0, ?)"
                .formatted(this.storage.table(LogTable.USERNAME));

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, time);
            statement.setInt(2, id);
            statement.setString(3, name);
            statement.executeUpdate();
        }
    }
}
