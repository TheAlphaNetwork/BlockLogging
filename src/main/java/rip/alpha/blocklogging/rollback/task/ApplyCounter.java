package rip.alpha.blocklogging.rollback.task;

import rip.alpha.blocklogging.rollback.model.ApplyResult;
import rip.alpha.blocklogging.rollback.model.BlockTarget;
import rip.alpha.blocklogging.rollback.model.Change;
import rip.alpha.blocklogging.rollback.model.ContainerChange;
import rip.alpha.blocklogging.rollback.model.EntityRemoval;
import rip.alpha.blocklogging.rollback.model.EntityRespawn;
import rip.alpha.blocklogging.rollback.model.HangingChange;
import rip.alpha.blocklogging.rollback.model.ItemChange;

final class ApplyCounter {

    private int blocks;
    private int hangings;
    private int containers;
    private int items;
    private int entities;
    private int skipped;

    void count(Change change, boolean applied) {

        if (!applied) {
            this.skipped++;
            return;
        }

        switch (change) {
            case BlockTarget _ -> this.blocks++;
            case HangingChange _ -> this.hangings++;
            case ContainerChange _ -> this.containers++;
            case ItemChange _ -> this.items++;
            case EntityRespawn _, EntityRemoval _ -> this.entities++;
        }
    }

    ApplyResult result(long millis) {
        return ApplyResult.builder()
                .blocks(this.blocks)
                .hangings(this.hangings)
                .containers(this.containers)
                .items(this.items)
                .entities(this.entities)
                .skipped(this.skipped)
                .millis(millis)
                .build();
    }
}
