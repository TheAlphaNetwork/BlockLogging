package rip.alpha.blocklogging.storage.sql.dialect;

import org.sqlite.JDBC;
import rip.alpha.blocklogging.config.StorageConfig;
import rip.alpha.blocklogging.storage.sql.model.Column;
import rip.alpha.blocklogging.storage.sql.model.IndexSpec;
import rip.alpha.blocklogging.storage.sql.model.TableSpec;
import rip.alpha.blocklogging.storage.sql.type.ColumnType;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public final class SqliteDialect implements Dialect {

    private static final String INDEX = "CREATE INDEX IF NOT EXISTS %s_%s ON %s (%s)";

    private final JDBC driver = new JDBC();

    @Override
    public String name() {
        return "SQLite";
    }

    @Override
    public Connection connect(StorageConfig config, Path folder) throws SQLException {
        Connection connection = this.driver.connect("jdbc:sqlite:" + folder.resolve(config.getFile()), new Properties());

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA journal_mode=WAL");
            statement.execute("PRAGMA synchronous=NORMAL");

        }

        return connection;
    }

    @Override
    public String columnType(ColumnType type) {
        return switch (type) {
            case ID -> "INTEGER PRIMARY KEY";
            case INT, BIGINT -> "INTEGER";
            case NAME -> "VARCHAR(64)";
            case TEXT -> "TEXT";
            case BLOB -> "BLOB";
        };
    }

    @Override
    public List<String> createTable(TableSpec spec, String prefix) {
        String table = prefix + spec.base();
        String columns = spec.columns().stream()
                .map(this::definition)
                .collect(Collectors.joining(", "));

        List<String> statements = new ArrayList<>();

        statements.add("CREATE TABLE IF NOT EXISTS %s (%s)".formatted(table, columns));

        for (IndexSpec index : spec.indexes())
            statements.add(INDEX.formatted(table, index.suffix(), table, String.join(", ", index.columns())));

        return statements;
    }

    @Override
    public List<String> optimize(List<String> tables) {
        return List.of("VACUUM");
    }

    private String definition(Column column) {
        return column.name() + " " + columnType(column.type());
    }
}
