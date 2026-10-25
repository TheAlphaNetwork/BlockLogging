package rip.alpha.blocklogging.storage.sql.state;

import lombok.RequiredArgsConstructor;
import rip.alpha.blocklogging.storage.sql.read.SqlRead;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public final class ReadPool {

    private static final int IDLE = 4;
    private static final int MAX_OPEN = 8;

    private static final long WAIT_SECONDS = 10;

    private final BlockingQueue<Connection> idle = new ArrayBlockingQueue<>(IDLE);

    private final Semaphore permits = new Semaphore(MAX_OPEN, true);

    private final ConnectionSource source;

    public <T> T read(SqlRead<T> read) throws SQLException {
        acquire();

        try {
            return attempt(read);
        } finally {
            this.permits.release();
        }
    }

    public void close() {
        Connection connection;

        while ((connection = this.idle.poll()) != null)
            discard(connection);
    }

    private <T> T attempt(SqlRead<T> read) throws SQLException {
        Connection connection = borrow();

        try {
            return run(connection, read);
        } catch (SQLException exception) {
            return run(this.source.open(), read);
        }
    }

    private <T> T run(Connection connection, SqlRead<T> read) throws SQLException {
        boolean healthy = false;

        try {
            T result = read.read(connection);

            healthy = true;
            return result;

        } finally {

            if (healthy)
                release(connection);
            else
                discard(connection);
        }
    }

    private void acquire() throws SQLException {

        try {

            if (!this.permits.tryAcquire(WAIT_SECONDS, TimeUnit.SECONDS))
                throw new SQLException("All %d read connections are busy".formatted(MAX_OPEN));

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new SQLException("Interrupted while waiting for a read connection", exception);
        }
    }

    private Connection borrow() throws SQLException {
        Connection connection = this.idle.poll();

        return connection == null || connection.isClosed() ? this.source.open() : connection;
    }

    private void release(Connection connection) {

        if (!this.idle.offer(connection))
            discard(connection);
    }

    private void discard(Connection connection) {

        try {
            connection.close();
        } catch (SQLException _) {
        }
    }
}
