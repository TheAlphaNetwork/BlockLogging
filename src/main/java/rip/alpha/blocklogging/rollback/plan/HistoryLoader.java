package rip.alpha.blocklogging.rollback.plan;

import lombok.RequiredArgsConstructor;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.query.model.ResolvedQuery;
import rip.alpha.blocklogging.query.model.TableFilter;
import rip.alpha.blocklogging.query.sql.FilterSql;
import rip.alpha.blocklogging.query.sql.SqlWhere;
import rip.alpha.blocklogging.record.codec.ItemCodec;
import rip.alpha.blocklogging.rollback.model.BlockHistory;
import rip.alpha.blocklogging.rollback.model.ContainerHistory;
import rip.alpha.blocklogging.rollback.model.KillHistory;
import rip.alpha.blocklogging.rollback.type.RollbackKind;
import rip.alpha.blocklogging.storage.StorageService;
import rip.alpha.blocklogging.storage.schema.Tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
final class HistoryLoader {

    private static final String SELECT = "SELECT t.id, w.name AS world_name, t.x, t.y, t.z, %s FROM %s t JOIN %s w ON w.id = t.world%s";
    private static final String ORDER = " ORDER BY t.time ASC, t.id ASC";

    private final Connection connection;

    private final StorageService storage;

    private final ResolvedQuery resolved;

    private final RollbackKind kind;

    List<BlockHistory> blocks(TableFilter filter) throws SQLException {
        return load(filter, "t.type, t.data, t.action, t.meta", result -> new BlockHistory(
                result.getLong("id"),
                position(result),
                result.getInt("type"),
                result.getInt("data"),
                result.getInt("action"),
                result.getBytes("meta")
        ));
    }

    List<ContainerHistory> containers(TableFilter filter) throws SQLException {
        return load(filter, "t.amount, t.action, t.item", result -> new ContainerHistory(
                result.getLong("id"),

                position(result),

                ItemCodec.decode(result.getBytes("item")),

                result.getInt("amount"),
                result.getInt("action")
        ));
    }

    List<KillHistory> kills(TableFilter filter) throws SQLException {
        return load(filter, "t.type", result -> new KillHistory(result.getLong("id"), position(result), result.getInt("type")));
    }

    private <T> List<T> load(TableFilter filter, String columns, RowMapper<T> mapper) throws SQLException {
        SqlWhere where = FilterSql.where(this.resolved, filter).add("rolled_back = ?", this.kind.matchedFlag());

        String table = this.storage.table(filter.table());
        String sql = SELECT.formatted(columns, table, this.storage.table(Tables.WORLD), where.sql()) + ORDER;

        List<T> rows = new ArrayList<>();

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            where.bind(statement, 1);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next())
                    rows.add(mapper.map(result));
            }
        }

        return rows;
    }

    private BlockPosition position(ResultSet result) throws SQLException {
        return new BlockPosition(
                result.getString("world_name"),
                result.getInt("x"),
                result.getInt("y"),
                result.getInt("z"));
    }
}
