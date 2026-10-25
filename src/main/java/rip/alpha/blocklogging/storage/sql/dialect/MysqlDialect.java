package rip.alpha.blocklogging.storage.sql.dialect;

import com.mysql.jdbc.Driver;
import rip.alpha.blocklogging.config.StorageConfig;
import rip.alpha.blocklogging.storage.sql.model.IndexSpec;
import rip.alpha.blocklogging.storage.sql.model.TableSpec;
import rip.alpha.blocklogging.storage.sql.type.ColumnType;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class MysqlDialect implements Dialect {

    @Override
    public String name() {
        return "MySQL";
    }

    @Override
    public Connection connect(StorageConfig config, Path folder) throws SQLException {
        String url = "jdbc:mysql://%s:%d/%s?%s".formatted(config.getHost(), config.getPort(), config.getDatabase(), config.getProperties());

        Properties properties = new Properties();

        properties.setProperty("user", config.getUsername());
        properties.setProperty("password", config.getPassword());

        return new Driver().connect(url, properties);
    }

    @Override
    public String columnType(ColumnType type) {
        return switch (type) {
            case ID -> "INT NOT NULL AUTO_INCREMENT PRIMARY KEY";
            case INT -> "INT";
            case BIGINT -> "BIGINT";
            case NAME -> "VARCHAR(64)";
            case TEXT -> "TEXT";
            case BLOB -> "MEDIUMBLOB";
        };
    }

    @Override
    public List<String> createTable(TableSpec spec, String prefix) {
        String table = prefix + spec.base();

        List<String> parts = new ArrayList<>();

        spec.columns().forEach(column -> parts.add(column.name() + " " + columnType(column.type())));

        for (IndexSpec index : spec.indexes())
            parts.add("INDEX %s_%s (%s)".formatted(table, index.suffix(), String.join(", ", index.columns())));

        String definition = String.join(", ", parts);

        return List.of("CREATE TABLE IF NOT EXISTS %s (%s) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4".formatted(table, definition));
    }

    @Override
    public List<String> optimize(List<String> tables) {
        return List.of("OPTIMIZE TABLE " + String.join(", ", tables));
    }
}
