package rip.alpha.blocklogging.storage.sql.read;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.query.model.ResolvedQuery;
import rip.alpha.blocklogging.query.model.TableFilter;
import rip.alpha.blocklogging.record.codec.ItemCodec;
import rip.alpha.blocklogging.rollback.model.BlockHistory;
import rip.alpha.blocklogging.rollback.model.ContainerHistory;
import rip.alpha.blocklogging.rollback.model.ItemHistory;
import rip.alpha.blocklogging.rollback.model.KillHistory;
import rip.alpha.blocklogging.rollback.type.RollbackKind;
import rip.alpha.blocklogging.storage.sql.SqlStore;
import rip.alpha.blocklogging.storage.sql.schema.Tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
final class HistoryLoader {

    private static final String SELECT = "SELECT t.id, w.name AS world_name, u.uuid AS user_uuid, t.x, t.y, t.z, %s FROM %s t ";
    private static final String JOINS = "JOIN %s w ON w.id = t.world JOIN %s u ON u.id = t.user%s ORDER BY t.time ASC, t.id ASC";

    private final Connection connection;

    private final SqlStore store;

    private final ResolvedQuery resolved;

    private final RollbackKind kind;

    List<BlockHistory> blocks(TableFilter filter) throws SQLException {
        return load(filter, "t.type, t.data, t.action, t.meta", result -> BlockHistory.builder()
                .id(result.getString("id"))
                .position(position(result))
                .type(result.getInt("type"))
                .data(result.getInt("data"))
                .action(result.getInt("action"))
                .meta(result.getBytes("meta"))
                .build());
    }

    List<ContainerHistory> containers(TableFilter filter) throws SQLException {
        return load(filter, "t.amount, t.action, t.item", result -> ContainerHistory.builder()
                .id(result.getString("id"))
                .position(position(result))
                .item(ItemCodec.decode(result.getBytes("item")))
                .amount(result.getInt("amount"))
                .action(result.getInt("action"))
                .build());
    }

    List<KillHistory> kills(TableFilter filter) throws SQLException {
        return load(filter, "t.type, t.meta, t.respawned", result -> KillHistory.builder()
                .id(result.getString("id"))
                .position(position(result))
                .type(result.getInt("type"))
                .meta(result.getBytes("meta"))
                .respawned(uuid(result.getString("respawned")))
                .build());
    }

    List<ItemHistory> items(TableFilter filter) throws SQLException {
        return load(filter, "t.amount, t.action, t.item", result -> ItemHistory.builder()
                .id(result.getString("id"))
                .player(uuid(result.getString("user_uuid")))
                .item(ItemCodec.decode(result.getBytes("item")))
                .amount(result.getInt("amount"))
                .action(result.getInt("action"))
                .build());
    }

    private <T> List<T> load(TableFilter filter, String columns, RowMapper<T> mapper) throws SQLException {
        SqlWhere where = FilterSql.where(this.resolved, filter).add("rolled_back = ?", this.kind.matchedFlag());

        String head = SELECT.formatted(columns, this.store.table(filter.table()));
        String sql = head + JOINS.formatted(this.store.table(Tables.WORLD), this.store.table(Tables.USER), where.sql());

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
        return BlockPosition.builder()
                .world(result.getString("world_name"))
                .x(result.getInt("x"))
                .y(result.getInt("y"))
                .z(result.getInt("z"))
                .build();
    }

    @Nullable
    private UUID uuid(@Nullable String value) {
        return value == null ? null : UUID.fromString(value);
    }
}
