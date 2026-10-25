package rip.alpha.blocklogging.record.write;

import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.storage.StorageService;
import rip.alpha.blocklogging.storage.state.UserIds;
import rip.alpha.blocklogging.storage.state.WorldIds;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

final class WriterSession {

    private final StorageService storage;

    private final Logger logger;

    private final UserIds users;

    private final WorldIds worlds;

    @Nullable
    private Connection connection;

    WriterSession(StorageService storage, Logger logger) {
        this.storage = storage;
        this.logger = logger;
        this.users = new UserIds(storage);
        this.worlds = new WorldIds(storage);
    }

    WriteContext context() throws SQLException {

        if (this.connection == null || this.connection.isClosed())
            this.connection = this.storage.connect();

        return new WriteContext(this.connection, this.storage, this.users, this.worlds);
    }

    void reset() {
        close();
        this.users.clear();
        this.worlds.clear();
    }

    void close() {

        if (this.connection == null)
            return;

        try {
            this.connection.close();
        } catch (SQLException _) {
            this.logger.fine("Could not close the writer connection");
        }

        this.connection = null;
    }
}
