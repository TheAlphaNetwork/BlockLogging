package rip.alpha.blocklogging.storage.sql.write;

import rip.alpha.blocklogging.record.codec.ItemCodec;
import rip.alpha.blocklogging.record.model.ContainerRecord;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

final class ContainerInsert implements RecordInsert<ContainerRecord> {

    @Override
    public LogTable table() {
        return LogTable.CONTAINER;
    }

    @Override
    public List<String> columns() {
        return List.of("type", "data", "amount", "action", "rolled_back", "item");
    }

    @Override
    @SuppressWarnings("deprecation")
    public void bind(PreparedStatement statement, int index, ContainerRecord record) throws SQLException, IOException {
        statement.setInt(index, record.item().getTypeId());
        statement.setInt(index + 1, record.item().getDurability());
        statement.setInt(index + 2, record.amount());
        statement.setInt(index + 3, record.action().getId());
        statement.setInt(index + 4, 0);
        statement.setBytes(index + 5, ItemCodec.encode(record.item()));
    }
}
