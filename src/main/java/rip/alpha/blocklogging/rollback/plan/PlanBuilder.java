package rip.alpha.blocklogging.rollback.plan;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.query.model.ResolvedQuery;
import rip.alpha.blocklogging.query.model.TableFilter;
import rip.alpha.blocklogging.query.sql.TableFilters;
import rip.alpha.blocklogging.record.model.RolledBackUpdate;
import rip.alpha.blocklogging.rollback.model.BlockHistory;
import rip.alpha.blocklogging.rollback.model.ContainerChange;
import rip.alpha.blocklogging.rollback.model.ContainerHistory;
import rip.alpha.blocklogging.rollback.model.EntityRespawn;
import rip.alpha.blocklogging.rollback.model.KillHistory;
import rip.alpha.blocklogging.rollback.model.RollbackPlan;
import rip.alpha.blocklogging.rollback.type.RollbackKind;
import rip.alpha.blocklogging.storage.StorageService;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@UtilityClass
@SuppressWarnings("deprecation")
public class PlanBuilder {

    private final Set<Integer> BLOCK_CHANGES = Set.of(0, 1);

    private final int CONTAINER_ADD = 1;

    public RollbackPlan build(Connection connection, StorageService storage, ResolvedQuery resolved, RollbackKind kind)
            throws SQLException {
        HistoryLoader loader = new HistoryLoader(connection, storage, resolved, kind);

        Map<LogTable, TableFilter> filters = new EnumMap<>(LogTable.class);

        TableFilters.of(resolved.query().actions()).forEach(filter -> filters.put(filter.table(), filter));

        TableFilter blockFilter = blockFilter(filters.get(LogTable.BLOCK));
        TableFilter containerFilter = filters.get(LogTable.CONTAINER);
        TableFilter killFilter = kind == RollbackKind.ROLLBACK ? filters.get(LogTable.KILL) : null;

        List<BlockHistory> blocks = blockFilter == null ? List.of() : loader.blocks(blockFilter);
        List<ContainerHistory> containers = containerFilter == null ? List.of() : loader.containers(containerFilter);
        List<KillHistory> kills = killFilter == null ? List.of() : loader.kills(killFilter);

        boolean rolledBack = kind == RollbackKind.ROLLBACK;
        List<RolledBackUpdate> flags = List.of(
                new RolledBackUpdate(LogTable.BLOCK, blocks.stream().map(BlockHistory::id).toList(), rolledBack),
                new RolledBackUpdate(LogTable.CONTAINER, containers.stream().map(ContainerHistory::id).toList(), rolledBack),
                new RolledBackUpdate(LogTable.KILL, kills.stream().map(KillHistory::id).toList(), rolledBack)
        );

        int matched = blocks.size() + containers.size() + kills.size();

        return new RollbackPlan(BlockPlanner.plan(blocks, kind), containerChanges(containers, kind), respawns(kills), flags, matched);
    }

    @Nullable
    private TableFilter blockFilter(@Nullable TableFilter filter) {

        if (filter == null)
            return null;

        Set<Integer> actions = new HashSet<>(filter.actions().isEmpty() ? BLOCK_CHANGES : filter.actions());

        actions.retainAll(BLOCK_CHANGES);

        return actions.isEmpty() ? null : new TableFilter(LogTable.BLOCK, Set.copyOf(actions));
    }

    private List<ContainerChange> containerChanges(List<ContainerHistory> history, RollbackKind kind) {
        List<ContainerChange> changes = new ArrayList<>(history.size());

        for (ContainerHistory row : history) {

            if (row.item() == null)
                continue;

            boolean added = row.action() == CONTAINER_ADD;
            boolean add = (kind == RollbackKind.ROLLBACK) != added;

            changes.add(new ContainerChange(row.position(), row.item(), row.amount(), add));
        }

        return changes;
    }

    private List<EntityRespawn> respawns(List<KillHistory> kills) {
        List<EntityRespawn> respawns = new ArrayList<>(kills.size());

        for (KillHistory kill : kills) {
            EntityType type = EntityType.fromId(kill.type());

            if (type != null && type.isAlive() && type.isSpawnable() && type != EntityType.PLAYER)
                respawns.add(new EntityRespawn(kill.position(), type));
        }

        return respawns;
    }
}
