package rip.alpha.blocklogging.query.filter;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.query.model.TableFilter;
import rip.alpha.blocklogging.query.type.Action;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@UtilityClass
public class TableFilters {

    public List<TableFilter> of(Set<Action> actions) {
        Map<LogTable, Set<Integer>> values = new EnumMap<>(LogTable.class);

        Set<LogTable> unrestricted = new HashSet<>();

        for (Action action : actions) {
            values.computeIfAbsent(action.getTable(), _ -> new HashSet<>()).addAll(action.getValues());

            if (action.getValues().isEmpty())
                unrestricted.add(action.getTable());
        }

        List<TableFilter> filters = new ArrayList<>();

        values.forEach((table, set) -> filters.add(new TableFilter(table, unrestricted.contains(table) ? Set.of() : Set.copyOf(set))));

        return filters;
    }
}
