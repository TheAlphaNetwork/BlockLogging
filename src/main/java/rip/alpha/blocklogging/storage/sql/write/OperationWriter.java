package rip.alpha.blocklogging.storage.sql.write;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.query.codec.QueryCodec;
import rip.alpha.blocklogging.record.model.OperationWrite;
import rip.alpha.blocklogging.rollback.model.OperationRecord;
import rip.alpha.blocklogging.storage.sql.schema.Tables;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@UtilityClass
class OperationWriter {

    private final String UPDATE = "UPDATE %s SET state = ?, time = ? WHERE uid = ?";
    private final String INSERT = "INSERT INTO %s (uid, sender, kind, state, time, query) VALUES (?, ?, ?, ?, ?, ?)";

    void apply(WriteContext context, List<OperationWrite> writes) throws SQLException, IOException {
        String table = context.getStorage().table(Tables.OPERATION);

        for (OperationWrite write : writes) {

            if (update(context, table, write.record()) == 0)
                insert(context, table, write.record());
        }
    }

    private int update(WriteContext context, String table, OperationRecord record) throws SQLException {

        try (PreparedStatement statement = context.getConnection().prepareStatement(UPDATE.formatted(table))) {
            statement.setInt(1, record.state().getId());
            statement.setLong(2, record.time());
            statement.setString(3, record.operation().id());

            return statement.executeUpdate();
        }
    }

    private void insert(WriteContext context, String table, OperationRecord record) throws SQLException, IOException {

        try (PreparedStatement statement = context.getConnection().prepareStatement(INSERT.formatted(table))) {
            statement.setString(1, record.operation().id());
            statement.setString(2, record.sender());
            statement.setInt(3, record.operation().kind().ordinal());
            statement.setInt(4, record.state().getId());
            statement.setLong(5, record.time());
            statement.setBytes(6, QueryCodec.encode(record.operation().query()));

            statement.executeUpdate();
        }
    }
}
