package rip.alpha.blocklogging.storage.model;

import rip.alpha.blocklogging.storage.type.ColumnType;

public record Column(String name, ColumnType type) {
}
