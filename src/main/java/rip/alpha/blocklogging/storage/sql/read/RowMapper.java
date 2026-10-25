package rip.alpha.blocklogging.storage.sql.read;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
interface RowMapper<T> {

    T map(ResultSet result) throws SQLException;
}
