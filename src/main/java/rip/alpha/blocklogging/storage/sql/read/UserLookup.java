package rip.alpha.blocklogging.storage.sql.read;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.storage.sql.SqlStore;
import rip.alpha.blocklogging.storage.sql.schema.Tables;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@UtilityClass
class UserLookup {

    Set<Integer> ids(Connection connection, SqlStore storage, String name) throws SQLException {
        String sql = "SELECT id FROM %s WHERE LOWER(name) = ? UNION SELECT user FROM %s WHERE LOWER(text) = ?"
                .formatted(storage.table(Tables.USER), storage.table(LogTable.USERNAME));

        String lower = name.toLowerCase(Locale.ROOT);

        Set<Integer> ids = new HashSet<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, lower);
            statement.setString(2, lower);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next())
                    ids.add(result.getInt(1));
            }
        }

        return ids;
    }
}
