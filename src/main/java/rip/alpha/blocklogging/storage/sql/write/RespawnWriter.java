package rip.alpha.blocklogging.storage.sql.write;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.record.model.RespawnUpdate;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@UtilityClass
class RespawnWriter {

    void apply(WriteContext context, List<RespawnUpdate> updates) throws SQLException {

        if (updates.isEmpty())
            return;

        String sql = "UPDATE %s SET respawned = ? WHERE id = ?".formatted(context.getStorage().table(LogTable.KILL));

        try (PreparedStatement statement = context.getConnection().prepareStatement(sql)) {

            for (RespawnUpdate update : updates) {
                statement.setString(1, update.entity() == null ? null : update.entity().toString());
                statement.setLong(2, Long.parseLong(update.id()));
                statement.addBatch();
            }

            statement.executeBatch();
        }
    }
}
