package rip.alpha.blocklogging.storage.sql.read;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.storage.sql.SqlStore;
import rip.alpha.blocklogging.storage.sql.schema.Tables;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class SignLookup {

    private final String SELECT = "SELECT s.text FROM %s s JOIN %s w ON w.id = s.world "
            .concat("WHERE w.name = ? AND s.x = ? AND s.y = ? AND s.z = ? ORDER BY s.time DESC, s.id DESC LIMIT 1");

    public Map<BlockPosition, String> latest(Connection connection, SqlStore store, Collection<BlockPosition> positions)
            throws SQLException {
        Map<BlockPosition, String> texts = new HashMap<>();

        String sql = SELECT.formatted(store.table(LogTable.SIGN), store.table(Tables.WORLD));

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            for (BlockPosition position : positions) {
                statement.setString(1, position.world());
                statement.setInt(2, position.x());
                statement.setInt(3, position.y());
                statement.setInt(4, position.z());

                try (ResultSet result = statement.executeQuery()) {

                    if (result.next())
                        texts.put(position, result.getString(1));
                }
            }
        }

        return texts;
    }
}
