package rip.alpha.blocklogging.rollback.model;

import rip.alpha.blocklogging.record.model.RolledBackUpdate;

import java.util.ArrayList;
import java.util.List;

public record RollbackPlan(
        List<BlockTarget> blocks,
        List<ContainerChange> containers,
        List<EntityRespawn> entities,
        List<RolledBackUpdate> flags,
        int matched
) {

    public boolean isEmpty() {
        return this.matched == 0;
    }

    public List<Change> changes() {
        List<Change> changes = new ArrayList<>(this.blocks.size() + this.containers.size() + this.entities.size());

        changes.addAll(this.blocks);
        changes.addAll(this.containers);
        changes.addAll(this.entities);

        return changes;
    }
}
