package rip.alpha.blocklogging.storage.sql.schema;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.storage.sql.model.Column;
import rip.alpha.blocklogging.storage.sql.model.IndexSpec;
import rip.alpha.blocklogging.storage.sql.model.TableSpec;
import rip.alpha.blocklogging.storage.sql.type.ColumnType;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Tables {

    public final String USER = "user";
    public final String WORLD = "world";
    public final String OPERATION = "operation";

    private final List<IndexSpec> LOG_INDEXES = List.of(
            new IndexSpec("location", List.of("world", "x", "z", "time")),
            new IndexSpec("actor", List.of("user", "time")),
            new IndexSpec("time", List.of("time"))
    );

    public List<TableSpec> all() {
        List<TableSpec> specs = new ArrayList<>();

        specs.add(new TableSpec(USER, List.of(id(), column("name", ColumnType.NAME), column("uuid", ColumnType.NAME)),
                List.of(new IndexSpec("name", List.of("name")), new IndexSpec("uuid", List.of("uuid")))));

        specs.add(new TableSpec(WORLD, List.of(id(), column("name", ColumnType.NAME)), List.of()));

        for (LogTable table : LogTable.values())
            specs.add(new TableSpec(table.getBase(), logColumns(table), LOG_INDEXES));

        specs.add(OperationTable.spec());

        return specs;
    }

    private List<Column> logColumns(LogTable table) {
        List<Column> columns = new ArrayList<>(List.of(
                id(),
                column("time", ColumnType.BIGINT),
                column("user", ColumnType.INT),
                column("world", ColumnType.INT),
                column("x", ColumnType.INT),
                column("y", ColumnType.INT),
                column("z", ColumnType.INT)
        ));

        columns.addAll(LogColumns.extra(table));
        return columns;
    }

    private Column id() {
        return column("id", ColumnType.ID);
    }

    private Column column(String name, ColumnType type) {
        return new Column(name, type);
    }
}
