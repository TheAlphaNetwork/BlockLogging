package rip.alpha.blocklogging.storage.sql.dialect;

import rip.alpha.blocklogging.config.StorageConfig;
import rip.alpha.blocklogging.storage.sql.model.TableSpec;
import rip.alpha.blocklogging.storage.sql.type.ColumnType;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public sealed interface Dialect permits SqliteDialect, MysqlDialect {

    static Dialect of(StorageConfig config) {
        return config.isMysql() ? new MysqlDialect() : new SqliteDialect();
    }

    String name();

    Connection connect(StorageConfig config, Path folder) throws SQLException;

    String columnType(ColumnType type);

    List<String> createTable(TableSpec spec, String prefix);

    List<String> optimize(List<String> tables);
}
