package rip.alpha.blocklogging.storage.sql.schema;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.storage.sql.model.Column;
import rip.alpha.blocklogging.storage.sql.model.IndexSpec;
import rip.alpha.blocklogging.storage.sql.model.TableSpec;
import rip.alpha.blocklogging.storage.sql.type.ColumnType;

import java.util.List;

@UtilityClass
class OperationTable {

    TableSpec spec() {
        List<Column> columns = List.of(
                new Column("id", ColumnType.ID),
                new Column("uid", ColumnType.NAME),
                new Column("sender", ColumnType.NAME),
                new Column("kind", ColumnType.INT),
                new Column("state", ColumnType.INT),
                new Column("time", ColumnType.BIGINT),
                new Column("query", ColumnType.BLOB)
        );

        return new TableSpec(Tables.OPERATION, columns, List.of(new IndexSpec("uid", List.of("uid"))));
    }
}
