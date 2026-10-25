package rip.alpha.blocklogging.storage.sql.write;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.record.model.RolledBackUpdate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@UtilityClass
class RolledBackWriter {

    private final int CHUNK = 500;

    void apply(WriteContext context, List<RolledBackUpdate> updates) throws SQLException {

        for (RolledBackUpdate update : updates) {
            List<String> ids = update.ids();

            for (int start = 0; start < ids.size(); start += CHUNK)
                apply(context, update, ids.subList(start, Math.min(ids.size(), start + CHUNK)));
        }
    }

    private void apply(WriteContext context, RolledBackUpdate update, List<String> ids) throws SQLException {
        String marks = String.join(", ", Collections.nCopies(ids.size(), "?"));
        String sql = "UPDATE %s SET rolled_back = ? WHERE id IN (%s)".formatted(context.getStorage().table(update.table()), marks);

        try (PreparedStatement statement = context.getConnection().prepareStatement(sql)) {
            statement.setInt(1, update.rolledBack() ? 1 : 0);

            for (int index = 0; index < ids.size(); index++)
                statement.setLong(index + 2, Long.parseLong(ids.get(index)));

            statement.executeUpdate();
        }
    }
}
