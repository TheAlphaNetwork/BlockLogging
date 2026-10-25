package rip.alpha.blocklogging.rollback.task;

import rip.alpha.blocklogging.rollback.model.ApplyResult;
import rip.alpha.blocklogging.rollback.model.BlockTarget;
import rip.alpha.blocklogging.rollback.model.Change;
import rip.alpha.blocklogging.rollback.model.ContainerChange;
import rip.alpha.blocklogging.rollback.model.EntityRespawn;

final class ApplyCounter {

    private int blocks;
    private int containers;
    private int entities;
    private int skipped;

    void count(Change change, boolean applied) {

        if (!applied) {
            this.skipped++;
            return;
        }

        switch (change) {
            case BlockTarget _ -> this.blocks++;
            case ContainerChange _ -> this.containers++;
            case EntityRespawn _ -> this.entities++;
        }
    }

    ApplyResult result(long millis) {
        return new ApplyResult(this.blocks, this.containers, this.entities, this.skipped, millis);
    }
}
