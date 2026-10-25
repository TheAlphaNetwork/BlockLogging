package rip.alpha.blocklogging.query.sql;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.query.model.AreaRadius;
import rip.alpha.blocklogging.query.model.ExactRadius;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.query.model.ResolvedQuery;
import rip.alpha.blocklogging.query.model.TableFilter;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.Set;

@UtilityClass
public class FilterSql {

    private final Set<LogTable> ACTION_TABLES = Set.of(LogTable.BLOCK, LogTable.CONTAINER, LogTable.ITEM, LogTable.SESSION);

    public SqlWhere where(ResolvedQuery resolved, TableFilter filter) {
        Query query = resolved.query();

        SqlWhere where = new SqlWhere();

        if (query.since() > 0)
            where.add("time >= ?", query.since());

        if (query.until() > 0)
            where.add("time <= ?", query.until());

        if (resolved.users() != null)
            where.in("user", resolved.users(), false);

        where.in("user", resolved.excludedUsers(), true);

        if (resolved.world() != null)
            where.add("world = ?", resolved.world());

        switch (query.radius()) {
            case AreaRadius area -> area(where, area);
            case ExactRadius exact -> exact(where, exact.position());
            default -> {
            }
        }

        if (filter.table().isTyped()) {
            where.in("type", query.include().forTable(filter.table()), false);
            where.in("type", query.exclude().forTable(filter.table()), true);
        }

        if (ACTION_TABLES.contains(filter.table()))
            where.in("action", filter.actions(), false);

        return where;
    }

    private void area(SqlWhere where, AreaRadius area) {
        where.add("x BETWEEN ? AND ?", area.x() - area.size(), area.x() + area.size());
        where.add("z BETWEEN ? AND ?", area.z() - area.size(), area.z() + area.size());
    }

    private void exact(SqlWhere where, BlockPosition position) {
        where.add("x = ?", position.x()).add("z = ?", position.z()).add("y = ?", position.y());
    }
}
