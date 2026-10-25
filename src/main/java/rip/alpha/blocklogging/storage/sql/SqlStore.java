package rip.alpha.blocklogging.storage.sql;

import lombok.Getter;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.config.StorageConfig;
import rip.alpha.blocklogging.query.model.LookupPage;
import rip.alpha.blocklogging.query.model.PageRequest;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.query.model.ResolvedQuery;
import rip.alpha.blocklogging.record.model.PendingWrite;
import rip.alpha.blocklogging.record.model.PurgeRequest;
import rip.alpha.blocklogging.record.model.PurgeResult;
import rip.alpha.blocklogging.rollback.model.History;
import rip.alpha.blocklogging.rollback.model.OperationRecord;
import rip.alpha.blocklogging.rollback.type.RollbackKind;
import rip.alpha.blocklogging.storage.exception.StoreException;
import rip.alpha.blocklogging.storage.sql.dialect.Dialect;
import rip.alpha.blocklogging.storage.sql.read.HistoryReader;
import rip.alpha.blocklogging.storage.sql.read.LookupSql;
import rip.alpha.blocklogging.storage.sql.read.OperationReader;
import rip.alpha.blocklogging.storage.sql.read.QueryResolver;
import rip.alpha.blocklogging.storage.sql.read.SignLookup;
import rip.alpha.blocklogging.storage.sql.read.SqlRead;
import rip.alpha.blocklogging.storage.sql.schema.SchemaInstaller;
import rip.alpha.blocklogging.storage.sql.write.SqlWriter;
import rip.alpha.blocklogging.storage.store.LogStore;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class SqlStore implements LogStore {

    private final StorageConfig config;

    private final Path folder;

    @Getter
    private final Dialect dialect;

    private final SqlWriter writer;

    public SqlStore(StorageConfig config, Path folder, Logger logger) {
        this.config = config;
        this.folder = folder;
        this.dialect = Dialect.of(config);
        this.writer = new SqlWriter(this, logger);
    }

    @Override
    public String name() {
        return this.dialect.name();
    }

    @Override
    public void open() throws StoreException {

        try {
            Files.createDirectories(this.folder);
            SchemaInstaller.install(this, this.config.getTablePrefix());

        } catch (SQLException | IOException exception) {
            throw new StoreException("Could not open the " + name() + " database", exception);
        }
    }

    @Override
    public void close() {
        this.writer.close();
    }

    @Override
    public int write(List<PendingWrite> writes) throws StoreException {
        return this.writer.write(writes);
    }

    @Override
    public PurgeResult purge(PurgeRequest request) throws StoreException {
        return this.writer.purge(request);
    }

    @Override
    public LookupPage lookup(Query query, PageRequest page) throws StoreException {
        return read("Lookup failed", connection -> {
            ResolvedQuery resolved = QueryResolver.resolve(connection, this, query);

            return new LookupSql(connection, this, resolved).page(page.page(), page.size());
        });
    }

    @Override
    public History history(Query query, RollbackKind kind) throws StoreException {
        return read("Could not load the history", connection -> {
            ResolvedQuery resolved = QueryResolver.resolve(connection, this, query);

            return HistoryReader.read(connection, this, resolved, kind);
        });
    }

    @Override
    public Map<BlockPosition, String> signs(Collection<BlockPosition> positions) throws StoreException {
        return read("Could not load sign text", connection -> SignLookup.latest(connection, this, positions));
    }

    @Override
    public List<OperationRecord> operations() throws StoreException {
        return read("Could not load rollback operations", connection -> OperationReader.read(connection, this));
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

    private <T> T read(String error, SqlRead<T> read) throws StoreException {

        try (Connection connection = connect()) {
            return read.read(connection);
        } catch (SQLException exception) {
            throw new StoreException(error, exception);
        }
    }

    public List<String> logTables() {
        return Arrays.stream(LogTable.values())
                .map(this::table)
                .toList();
    }
}
