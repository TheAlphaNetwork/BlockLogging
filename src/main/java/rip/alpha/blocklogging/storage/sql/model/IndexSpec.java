package rip.alpha.blocklogging.storage.sql.model;

import java.util.List;

public record IndexSpec(String suffix, List<String> columns) {
}
