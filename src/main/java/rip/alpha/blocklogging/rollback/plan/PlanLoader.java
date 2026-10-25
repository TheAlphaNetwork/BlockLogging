package rip.alpha.blocklogging.rollback.plan;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.rollback.model.History;
import rip.alpha.blocklogging.rollback.model.Operation;
import rip.alpha.blocklogging.rollback.model.RollbackPlan;
import rip.alpha.blocklogging.storage.exception.StoreException;
import rip.alpha.blocklogging.storage.store.LogStore;

import java.util.Map;
import java.util.Set;

@UtilityClass
public class PlanLoader {

    public RollbackPlan load(LogStore store, Operation operation) throws StoreException {
        History history = store.history(operation.query(), operation.kind());

        Set<BlockPosition> positions = PlanBuilder.signPositions(history, operation.kind());

        Map<BlockPosition, String> signs = positions.isEmpty() ? Map.of() : store.signs(positions);

        return PlanBuilder.build(history, operation.kind(), signs);
    }
}
