package rip.alpha.blocklogging.storage.sql.read;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.query.filter.RollbackFilters;
import rip.alpha.blocklogging.query.model.ResolvedQuery;
import rip.alpha.blocklogging.query.model.TableFilter;
import rip.alpha.blocklogging.rollback.model.History;
import rip.alpha.blocklogging.rollback.type.RollbackKind;
import rip.alpha.blocklogging.storage.sql.SqlStore;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@UtilityClass
public class HistoryReader {

    public History read(Connection connection, SqlStore store, ResolvedQuery resolved, RollbackKind kind) throws SQLException {
        HistoryLoader loader = new HistoryLoader(connection, store, resolved, kind);

        Map<LogTable, TableFilter> filters = RollbackFilters.of(resolved.query().actions());

        TableFilter blocks = filters.get(LogTable.BLOCK);
        TableFilter containers = filters.get(LogTable.CONTAINER);
        TableFilter kills = filters.get(LogTable.KILL);
        TableFilter items = filters.get(LogTable.ITEM);

        return History.builder()
                .blocks(blocks == null ? List.of() : loader.blocks(blocks))
                .containers(containers == null ? List.of() : loader.containers(containers))
                .kills(kills == null ? List.of() : loader.kills(kills))
                .items(items == null ? List.of() : loader.items(items))
                .build();
    }
}
