package rip.alpha.blocklogging.storage.sql.write;

import lombok.RequiredArgsConstructor;
import rip.alpha.blocklogging.record.model.LogRecord;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
final class TextInsert<T extends LogRecord> implements RecordInsert<T> {

    private final LogTable table;

    private final Function<T, String> text;

    @Override
    public LogTable table() {
        return this.table;
    }

    @Override
    public List<String> columns() {
        return List.of("text");
    }

    @Override
    public void bind(PreparedStatement statement, int index, T record) throws SQLException {
        statement.setString(index, this.text.apply(record));
    }
}
