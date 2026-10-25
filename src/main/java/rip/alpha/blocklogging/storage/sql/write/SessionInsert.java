package rip.alpha.blocklogging.storage.sql.write;

import rip.alpha.blocklogging.record.model.SessionRecord;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

final class SessionInsert implements RecordInsert<SessionRecord> {

    @Override
    public LogTable table() {
        return LogTable.SESSION;
    }

    @Override
    public List<String> columns() {
        return List.of("action");
    }

    @Override
    public void bind(PreparedStatement statement, int index, SessionRecord record) throws SQLException {
        statement.setInt(index, record.action().getId());
    }
}
