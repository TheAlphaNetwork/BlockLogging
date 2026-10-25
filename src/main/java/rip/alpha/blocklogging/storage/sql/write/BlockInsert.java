package rip.alpha.blocklogging.storage.sql.write;

import rip.alpha.blocklogging.record.codec.BlockMetaCodec;
import rip.alpha.blocklogging.record.model.BlockRecord;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

final class BlockInsert implements RecordInsert<BlockRecord> {

    @Override
    public LogTable table() {
        return LogTable.BLOCK;
    }

    @Override
    public List<String> columns() {
        return List.of("type", "data", "action", "rolled_back", "meta");
    }

    @Override
    public void bind(PreparedStatement statement, int index, BlockRecord record) throws SQLException, IOException {
        statement.setInt(index, record.type());
        statement.setInt(index + 1, record.data());
        statement.setInt(index + 2, record.action().getId());
        statement.setInt(index + 3, 0);
        statement.setBytes(index + 4, BlockMetaCodec.encode(record.meta()));
    }
}
