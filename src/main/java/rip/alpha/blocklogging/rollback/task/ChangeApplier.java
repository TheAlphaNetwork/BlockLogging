package rip.alpha.blocklogging.rollback.task;

import lombok.RequiredArgsConstructor;
import rip.alpha.blocklogging.record.model.PendingWrite;
import rip.alpha.blocklogging.record.model.RespawnUpdate;
import rip.alpha.blocklogging.rollback.apply.BlockApplier;
import rip.alpha.blocklogging.rollback.apply.ContainerApplier;
import rip.alpha.blocklogging.rollback.apply.EntityApplier;
import rip.alpha.blocklogging.rollback.apply.HangingApplier;
import rip.alpha.blocklogging.rollback.apply.ItemApplier;
import rip.alpha.blocklogging.rollback.model.BlockTarget;
import rip.alpha.blocklogging.rollback.model.Change;
import rip.alpha.blocklogging.rollback.model.ContainerChange;
import rip.alpha.blocklogging.rollback.model.EntityRemoval;
import rip.alpha.blocklogging.rollback.model.EntityRespawn;
import rip.alpha.blocklogging.rollback.model.HangingChange;
import rip.alpha.blocklogging.rollback.model.ItemChange;

import java.util.UUID;
import java.util.function.Consumer;

@RequiredArgsConstructor
final class ChangeApplier {

    private final Consumer<PendingWrite> sink;

    boolean apply(Change change) {

        try {
            return switch (change) {
                case BlockTarget target -> BlockApplier.apply(target);
                case HangingChange hanging -> HangingApplier.apply(hanging);
                case ContainerChange container -> ContainerApplier.apply(container);
                case ItemChange item -> ItemApplier.apply(item);
                case EntityRespawn respawn -> respawn(respawn);
                case EntityRemoval removal -> remove(removal);
            };

        } catch (RuntimeException _) {
            return false;
        }
    }

    private boolean respawn(EntityRespawn respawn) {
        UUID entity = EntityApplier.spawn(respawn);

        if (entity != null)
            this.sink.accept(new RespawnUpdate(respawn.id(), entity));

        return entity != null;
    }

    private boolean remove(EntityRemoval removal) {
        boolean removed = EntityApplier.remove(removal);

        this.sink.accept(new RespawnUpdate(removal.id(), null));

        return removed;
    }
}
