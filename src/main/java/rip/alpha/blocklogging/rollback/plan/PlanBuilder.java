package rip.alpha.blocklogging.rollback.plan;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.type.BlockAction;
import rip.alpha.blocklogging.rollback.model.BlockHistory;
import rip.alpha.blocklogging.rollback.model.Change;
import rip.alpha.blocklogging.rollback.model.History;
import rip.alpha.blocklogging.rollback.model.RollbackPlan;
import rip.alpha.blocklogging.rollback.type.RollbackKind;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@UtilityClass
public class PlanBuilder {

    public RollbackPlan build(History history, RollbackKind kind, Map<BlockPosition, String> signs) {
        List<BlockHistory> blocks = new ArrayList<>();
        List<BlockHistory> hangings = new ArrayList<>();

        for (BlockHistory row : history.blocks())
            (HangingPlanner.isHanging(row.type()) ? hangings : blocks).add(row);

        List<Change> changes = new ArrayList<>(BlockPlanner.plan(blocks, kind, signs));

        changes.addAll(HangingPlanner.plan(hangings, kind));
        changes.addAll(ContainerPlanner.plan(history.containers(), kind));
        changes.addAll(ItemPlanner.plan(history.items(), kind));
        changes.addAll(EntityPlanner.plan(history.kills(), kind));

        return new RollbackPlan(List.copyOf(changes), history.size());
    }

    public Set<BlockPosition> signPositions(History history, RollbackKind kind) {

        if (kind != RollbackKind.RESTORE)
            return Set.of();

        Set<BlockPosition> positions = new HashSet<>();

        for (BlockHistory row : history.blocks()) {

            if (row.action() == BlockAction.PLACE.getId() && BlockPlanner.isSign(row.type()))
                positions.add(row.position());
        }

        return positions;
    }
}
