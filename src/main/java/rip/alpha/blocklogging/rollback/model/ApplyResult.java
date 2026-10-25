package rip.alpha.blocklogging.rollback.model;

public record ApplyResult(int blocks, int containers, int entities, int skipped, long millis) {
}
