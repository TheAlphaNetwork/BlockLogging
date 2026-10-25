package rip.alpha.blocklogging.storage.sql.read;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.query.codec.QueryReader;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.rollback.model.Operation;
import rip.alpha.blocklogging.rollback.model.OperationRecord;
import rip.alpha.blocklogging.rollback.type.OperationState;
import rip.alpha.blocklogging.rollback.type.RollbackKind;
import rip.alpha.blocklogging.storage.sql.SqlStore;
import rip.alpha.blocklogging.storage.sql.schema.Tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class OperationReader {

    private final String SELECT = "SELECT uid, sender, kind, state, time, query FROM %s ORDER BY time ASC";

    public List<OperationRecord> read(Connection connection, SqlStore store) throws SQLException {
        List<OperationRecord> records = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(SELECT.formatted(store.table(Tables.OPERATION)));
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                Query query = QueryReader.decode(result.getBytes("query"));

                if (query != null)
                    records.add(record(result, query));
            }
        }

        return records;
    }

    private OperationRecord record(ResultSet result, Query query) throws SQLException {
        Operation operation = new Operation(result.getString("uid"), query, RollbackKind.values()[result.getInt("kind")]);

        return OperationRecord.builder()
                .operation(operation)
                .sender(result.getString("sender"))
                .state(OperationState.of(result.getInt("state")))
                .time(result.getLong("time"))
                .build();
    }
}
