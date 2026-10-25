package rip.alpha.blocklogging.storage;

import lombok.Getter;
import rip.alpha.blocklogging.config.StorageConfig;
import rip.alpha.blocklogging.storage.dialect.Dialect;
import rip.alpha.blocklogging.storage.model.TableSpec;
import rip.alpha.blocklogging.storage.schema.Tables;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public final class StorageService {

    private final StorageConfig config;

    private final Path folder;

    @Getter
    private final Dialect dialect;

    public StorageService(StorageConfig config, Path folder) {
        this.config = config;
        this.folder = folder;
        this.dialect = Dialect.of(config);
    }

    public void install() throws SQLException, IOException {
        Files.createDirectories(this.folder);

        try (Connection connection = connect(); Statement statement = connection.createStatement()) {

            for (TableSpec spec : Tables.all()) {

                for (String sql : this.dialect.createTable(spec, this.config.getTablePrefix()))
                    statement.execute(sql);
            }
        }
    }

    public Connection connect() throws SQLException {
        return this.dialect.connect(this.config, this.folder);
    }

    public String table(String base) {
        return this.config.getTablePrefix() + base;
    }

    public String table(LogTable table) {
        return table(table.getBase());
    }

    public List<String> logTables() {
        return Arrays.stream(LogTable.values())
                .map(this::table)
                .toList();
    }
}
