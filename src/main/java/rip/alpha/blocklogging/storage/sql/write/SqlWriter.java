package rip.alpha.blocklogging.storage.sql.write;

import rip.alpha.blocklogging.record.model.PendingWrite;
import rip.alpha.blocklogging.record.model.PurgeRequest;
import rip.alpha.blocklogging.record.model.PurgeResult;
import rip.alpha.blocklogging.storage.exception.StoreException;
import rip.alpha.blocklogging.storage.sql.SqlStore;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public final class SqlWriter {

    private final WriterSession session;

    public SqlWriter(SqlStore store, Logger logger) {
        this.session = new WriterSession(store, logger);
    }

    public int write(List<PendingWrite> writes) throws StoreException {

        try {
            return SqlWrites.write(this.session, writes);

        } catch (SQLException | IOException exception) {
            this.session.reset();

            throw new StoreException("Could not write a batch of logs", exception);
        }
    }

    public PurgeResult purge(PurgeRequest request) throws StoreException {

        try {
            return PurgeWriter.purge(this.session, request);

        } catch (SQLException exception) {
            this.session.reset();

            throw new StoreException("Could not purge logs", exception);
        }
    }

    public void close() {
        this.session.close();
    }
}
