package rip.alpha.blocklogging.storage;

import lombok.Getter;
import rip.alpha.blocklogging.config.StorageConfig;
import rip.alpha.blocklogging.storage.exception.StoreException;
import rip.alpha.blocklogging.storage.mongo.MongoStore;
import rip.alpha.blocklogging.storage.sql.SqlStore;
import rip.alpha.blocklogging.storage.store.LogStore;

import java.nio.file.Path;
import java.util.logging.Logger;

@Getter
public final class StorageService {

    private final LogStore store;

    public StorageService(StorageConfig config, Path folder, Logger logger) {
        this.store = config.isMongo() ? new MongoStore(config) : new SqlStore(config, folder, logger);
    }

    public void open() throws StoreException {
        this.store.open();
    }

    public void close() {
        this.store.close();
    }

    public String name() {
        return this.store.name();
    }
}
