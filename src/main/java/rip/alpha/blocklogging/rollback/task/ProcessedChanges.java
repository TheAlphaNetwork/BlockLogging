package rip.alpha.blocklogging.rollback.task;

import lombok.RequiredArgsConstructor;
import rip.alpha.blocklogging.record.model.PendingWrite;
import rip.alpha.blocklogging.record.model.RolledBackUpdate;
import rip.alpha.blocklogging.rollback.model.Change;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@RequiredArgsConstructor
final class ProcessedChanges {

    private final Map<LogTable, List<String>> ids = new EnumMap<>(LogTable.class);

    private final Consumer<PendingWrite> sink;

    private final boolean rolledBack;

    void add(Change change) {
        this.ids.computeIfAbsent(change.table(), _ -> new ArrayList<>()).addAll(change.ids());
    }

    void flush() {
        this.ids.forEach((table, list) -> {

            if (!list.isEmpty())
                this.sink.accept(new RolledBackUpdate(table, List.copyOf(list), this.rolledBack));
        });

        this.ids.clear();
    }
}
