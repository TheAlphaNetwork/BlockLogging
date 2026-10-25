package rip.alpha.blocklogging.record.write;

import rip.alpha.blocklogging.record.model.KillRecord;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

final class KillInsert implements RecordInsert<KillRecord> {

    @Override
    public LogTable table() {
        return LogTable.KILL;
    }

    @Override
    public List<String> columns() {
        return List.of("type", "rolled_back");
    }

    @Override
    @SuppressWarnings("deprecation")
    public void bind(PreparedStatement statement, int index, KillRecord record) throws SQLException {
        statement.setInt(index, record.type().getTypeId());
        statement.setInt(index + 1, 0);
    }
}
