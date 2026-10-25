package rip.alpha.blocklogging.record.model;

import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.List;

public record RolledBackUpdate(LogTable table, List<String> ids, boolean rolledBack) implements PendingWrite {
}
