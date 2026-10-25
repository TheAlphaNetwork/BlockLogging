package rip.alpha.blocklogging.rollback.plan;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.codec.BlockMetaCodec;
import rip.alpha.blocklogging.record.type.BlockAction;
import rip.alpha.blocklogging.rollback.model.BlockHistory;
import rip.alpha.blocklogging.rollback.model.BlockTarget;
import rip.alpha.blocklogging.rollback.type.RollbackKind;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
class BlockPlanner {

    private final int AIR = 0;

    List<BlockTarget> plan(List<BlockHistory> history, RollbackKind kind) {
        Map<BlockPosition, BlockHistory> decisive = new HashMap<>();

        for (BlockHistory row : history) {

            if (kind == RollbackKind.ROLLBACK)
                decisive.putIfAbsent(row.position(), row);
            else
                decisive.put(row.position(), row);
        }

        List<BlockTarget> targets = new ArrayList<>(decisive.size());

        for (BlockHistory row : decisive.values())
            targets.add(kind == RollbackKind.ROLLBACK ? before(row) : after(row));

        targets.sort(Comparator.comparingInt(target -> target.position().y()));
        return targets;
    }

    private BlockTarget before(BlockHistory row) {

        if (row.action() == BlockAction.PLACE.getId())
            return new BlockTarget(row.position(), AIR, 0, null);

        return new BlockTarget(row.position(), row.type(), row.data(), BlockMetaCodec.decode(row.meta()));
    }

    private BlockTarget after(BlockHistory row) {

        if (row.action() == BlockAction.BREAK.getId())
            return new BlockTarget(row.position(), AIR, 0, null);

        return new BlockTarget(row.position(), row.type(), row.data(), null);
    }
}
