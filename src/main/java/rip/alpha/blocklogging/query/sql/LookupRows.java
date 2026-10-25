package rip.alpha.blocklogging.query.sql;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.query.model.LookupRow;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.sql.ResultSet;
import java.sql.SQLException;

@UtilityClass
class LookupRows {

    LookupRow read(ResultSet result) throws SQLException {
        String world = result.getString("world_name");

        BlockPosition position = new BlockPosition(world == null ? "" : world, result.getInt("x"), result.getInt("y"), result.getInt("z"));

        return new LookupRow(
                LogTable.values()[result.getInt("log")],

                result.getLong("time"),
                result.getString("user_name"),

                position,

                result.getInt("type"),
                result.getInt("data"),
                result.getInt("amount"),
                result.getInt("action"),
                result.getInt("rolled_back") == 1,
                result.getString("text")
        );
    }
}
