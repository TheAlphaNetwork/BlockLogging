package rip.alpha.blocklogging.query.render;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.query.model.LookupPage;
import rip.alpha.blocklogging.query.model.LookupRow;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@UtilityClass
public class ReplacedRows {

    private final Set<LogTable> TABLES = EnumSet.of(LogTable.BLOCK, LogTable.CONTAINER);

    private final int REMOVED = 0;
    private final int ADDED = 1;

    private final long WINDOW_SECONDS = 1;

    public LookupPage merge(LookupPage page) {
        List<LookupRow> rows = page.rows();
        List<LookupRow> merged = new ArrayList<>(rows.size());

        int index = 0;

        while (index < rows.size()) {
            LookupRow row = rows.get(index);
            LookupRow next = index + 1 < rows.size() ? rows.get(index + 1) : null;

            if (next != null && pair(row, next)) {
                merged.add(combine(row, next));
                index += 2;
                continue;
            }

            merged.add(row);
            index++;
        }

        return merged.size() == rows.size() ? page : page.toBuilder().rows(List.copyOf(merged)).build();
    }

    private boolean pair(LookupRow first, LookupRow second) {

        if (!TABLES.contains(first.table()) || first.table() != second.table())
            return false;

        if (first.action() + second.action() != REMOVED + ADDED || first.rolledBack() != second.rolledBack())
            return false;

        if (Math.abs(first.time() - second.time()) > WINDOW_SECONDS || !first.position().equals(second.position()))
            return false;

        boolean sameUser = Objects.equals(first.uuid(), second.uuid()) && first.user().equals(second.user());

        return sameUser && (first.type() != second.type() || first.data() != second.data());
    }

    private LookupRow combine(LookupRow first, LookupRow second) {
        LookupRow added = first.action() == ADDED ? first : second;
        LookupRow removed = added == first ? second : first;

        return added.toBuilder().replaced(removed).build();
    }
}
