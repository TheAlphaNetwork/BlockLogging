package rip.alpha.blocklogging.storage.sql.state;

import lombok.RequiredArgsConstructor;
import rip.alpha.blocklogging.storage.sql.read.SqlRead;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@RequiredArgsConstructor
public final class ReadPool {

    private static final int IDLE = 4;

    private final BlockingQueue<Connection> idle = new ArrayBlockingQueue<>(IDLE);

    private final ConnectionSource source;

    public <T> T read(SqlRead<T> read) throws SQLException {
        Connection connection = borrow();

        try {
            T result = read.read(connection);

            release(connection);
            return result;

        } catch (SQLException exception) {
            discard(connection);

            Connection fresh = this.source.open();

            try {
                T result = read.read(fresh);

                release(fresh);
                return result;

            } catch (SQLException retry) {
                discard(fresh);
                throw retry;
            }
        }
    }

    public void close() {
        Connection connection;

        while ((connection = this.idle.poll()) != null)
            discard(connection);
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
