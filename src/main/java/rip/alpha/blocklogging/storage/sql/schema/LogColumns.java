package rip.alpha.blocklogging.storage.sql.schema;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.storage.sql.model.Column;
import rip.alpha.blocklogging.storage.sql.type.ColumnType;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.List;

@UtilityClass
public class LogColumns {

    private final Column TYPE = new Column("type", ColumnType.INT);
    private final Column DATA = new Column("data", ColumnType.INT);
    private final Column AMOUNT = new Column("amount", ColumnType.INT);
    private final Column ACTION = new Column("action", ColumnType.INT);
    private final Column ROLLED_BACK = new Column("rolled_back", ColumnType.INT);
    private final Column TEXT = new Column("text", ColumnType.TEXT);

    public List<Column> extra(LogTable table) {
        return switch (table) {
            case BLOCK -> List.of(TYPE, DATA, ACTION, ROLLED_BACK, new Column("meta", ColumnType.BLOB));
            case CONTAINER -> List.of(TYPE, DATA, AMOUNT, ACTION, ROLLED_BACK, new Column("item", ColumnType.BLOB));
            case ITEM -> List.of(TYPE, DATA, AMOUNT, ACTION, ROLLED_BACK, new Column("item", ColumnType.BLOB));
            case KILL -> List.of(TYPE, ROLLED_BACK, new Column("meta", ColumnType.BLOB), new Column("respawned", ColumnType.NAME));
            case SESSION -> List.of(ACTION);
            case SIGN, CHAT, COMMAND, USERNAME -> List.of(TEXT);
        };
    }
}
