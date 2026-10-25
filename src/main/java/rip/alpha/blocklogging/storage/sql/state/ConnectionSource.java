package rip.alpha.blocklogging.storage.sql.state;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface ConnectionSource {

    Connection open() throws SQLException;
}
