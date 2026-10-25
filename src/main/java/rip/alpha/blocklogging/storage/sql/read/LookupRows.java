package rip.alpha.blocklogging.storage.sql.read;

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

        BlockPosition position = BlockPosition.builder()
                .world(world == null ? "" : world)
                .x(result.getInt("x"))
                .y(result.getInt("y"))
                .z(result.getInt("z"))
                .build();

        return LookupRow.builder()
                .table(LogTable.values()[result.getInt("log")])
                .time(result.getLong("time"))
                .user(result.getString("user_name"))
                .position(position)
                .type(result.getInt("type"))
                .data(result.getInt("data"))
                .amount(result.getInt("amount"))
                .action(result.getInt("action"))
                .rolledBack(result.getInt("rolled_back") == 1)
                .text(result.getString("text"))
                .build();
    }
}
