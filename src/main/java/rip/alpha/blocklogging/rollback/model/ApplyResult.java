package rip.alpha.blocklogging.rollback.model;

import lombok.Builder;

@Builder
public record ApplyResult(int blocks, int hangings, int containers, int items, int entities, int skipped, long millis) {
}
