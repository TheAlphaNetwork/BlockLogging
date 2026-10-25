package rip.alpha.blocklogging.storage.sql.write;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.record.model.PurgeRequest;
import rip.alpha.blocklogging.record.model.PurgeResult;
import rip.alpha.blocklogging.storage.sql.SqlStore;
import rip.alpha.blocklogging.storage.sql.state.WorldIds;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class PurgeWriter {

    public PurgeResult purge(WriterSession session, PurgeRequest request) throws SQLException {
        return purge(session.context(), request);
    }

    private PurgeResult purge(WriteContext context, PurgeRequest request) throws SQLException {
        long started = System.currentTimeMillis();

        SqlStore storage = context.getStorage();

        Connection connection = context.getConnection();

        Integer world = request.world() == null ? null : WorldIds.find(connection, storage, request.world());

        if (request.world() != null && world == null)
            return new PurgeResult(0, 0L, false);

        int rows = 0;

        for (String table : tables(storage))
            rows += delete(connection, table, request.before(), world);

        if (request.optimize()) {

            try (Statement statement = connection.createStatement()) {

                for (String sql : storage.getDialect().optimize(tables(storage)))
                    statement.execute(sql);
            }
        }

        return new PurgeResult(rows, System.currentTimeMillis() - started, false);
    }

    private int delete(Connection connection, String table, long before, Integer world) throws SQLException {
        String sql = "DELETE FROM %s WHERE time < ?%s".formatted(table, world == null ? "" : " AND world = ?");

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, before);

            if (world != null)
                statement.setInt(2, world);

            return statement.executeUpdate();
        }
    }

    private List<String> tables(SqlStore storage) {
        return Arrays.stream(LogTable.values())
                .map(storage::table)
                .toList();
    }
}
