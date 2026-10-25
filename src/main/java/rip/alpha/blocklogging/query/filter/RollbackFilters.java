package rip.alpha.blocklogging.query.filter;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.query.model.TableFilter;
import rip.alpha.blocklogging.query.type.Action;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@UtilityClass
public class RollbackFilters {

    private final Set<Integer> BLOCK_CHANGES = Set.of(0, 1);

    public Map<LogTable, TableFilter> of(Set<Action> actions) {
        Map<LogTable, TableFilter> filters = new EnumMap<>(LogTable.class);

        for (TableFilter filter : TableFilters.of(actions)) {

            if (filter.table().isReversible())
                filters.put(filter.table(), filter);
        }

        TableFilter blocks = blocks(filters.get(LogTable.BLOCK));

        if (blocks == null)
            filters.remove(LogTable.BLOCK);
        else
            filters.put(LogTable.BLOCK, blocks);

        return filters;
    }

    @Nullable
    private TableFilter blocks(@Nullable TableFilter filter) {

        if (filter == null)
            return null;

        Set<Integer> actions = new HashSet<>(filter.actions().isEmpty() ? BLOCK_CHANGES : filter.actions());

        actions.retainAll(BLOCK_CHANGES);

        return actions.isEmpty() ? null : new TableFilter(LogTable.BLOCK, Set.copyOf(actions));
    }
}
