package rip.alpha.blocklogging.storage.sql.read;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface SqlRead<T> {

    T read(Connection connection) throws SQLException;
}
