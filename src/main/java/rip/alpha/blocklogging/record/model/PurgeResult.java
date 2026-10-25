package rip.alpha.blocklogging.record.model;

public record PurgeResult(int rows, long millis, boolean failed) {
}
