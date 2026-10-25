package rip.alpha.blocklogging.rollback.plan;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
interface RowMapper<T> {

    T map(ResultSet result) throws SQLException;
}
