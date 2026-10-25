package rip.alpha.blocklogging.storage.sql.write;

import rip.alpha.blocklogging.record.model.LogRecord;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

interface RecordInsert<T extends LogRecord> {

    LogTable table();

    List<String> columns();

    void bind(PreparedStatement statement, int index, T record) throws SQLException, IOException;
}
