package rip.alpha.blocklogging.storage.sql.model;

import java.util.List;

public record TableSpec(String base, List<Column> columns, List<IndexSpec> indexes) {
}
