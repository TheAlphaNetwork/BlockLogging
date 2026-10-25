package rip.alpha.blocklogging.storage.sql.model;

import rip.alpha.blocklogging.storage.sql.type.ColumnType;

public record Column(String name, ColumnType type) {
}
