package rip.alpha.blocklogging.rollback.model;

import java.util.List;

public record RollbackPlan(List<Change> changes, int matched) {

    public boolean isEmpty() {
        return this.matched == 0;
    }

    public List<BlockTarget> blocks() {
        return this.changes.stream()
                .filter(BlockTarget.class::isInstance)
                .map(BlockTarget.class::cast)
                .toList();
    }

    public long count(Class<? extends Change> type) {
        return this.changes.stream().filter(type::isInstance).count();
    }
}
