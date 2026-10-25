package rip.alpha.blocklogging.record.write;

import rip.alpha.blocklogging.record.codec.ItemCodec;
import rip.alpha.blocklogging.record.model.ItemRecord;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

final class ItemInsert implements RecordInsert<ItemRecord> {

    @Override
    public LogTable table() {
        return LogTable.ITEM;
    }

    @Override
    public List<String> columns() {
        return List.of("type", "data", "amount", "action", "item");
    }

    @Override
    @SuppressWarnings("deprecation")
    public void bind(PreparedStatement statement, int index, ItemRecord record) throws SQLException, IOException {
        statement.setInt(index, record.item().getTypeId());
        statement.setInt(index + 1, record.item().getDurability());
        statement.setInt(index + 2, record.amount());
        statement.setInt(index + 3, record.action().getId());
        statement.setBytes(index + 4, ItemCodec.encode(record.item()));
    }
}
