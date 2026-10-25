package rip.alpha.blocklogging.query.model;

import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.Set;

public record TableFilter(LogTable table, Set<Integer> actions) {
}
