package rip.alpha.blocklogging.storage.model;

import java.util.List;

public record IndexSpec(String suffix, List<String> columns) {
}
