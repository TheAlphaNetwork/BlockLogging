package rip.alpha.blocklogging.rollback.plan;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import rip.alpha.blocklogging.record.codec.BlockMetaCodec;
import rip.alpha.blocklogging.record.model.HangingData;
import rip.alpha.blocklogging.record.type.BlockAction;
import rip.alpha.blocklogging.rollback.model.BlockHistory;
import rip.alpha.blocklogging.rollback.model.HangingChange;
import rip.alpha.blocklogging.rollback.model.HangingKey;
import rip.alpha.blocklogging.rollback.type.RollbackKind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@UtilityClass
@SuppressWarnings("deprecation")
class HangingPlanner {

    private final Set<Integer> TYPES = Set.of(Material.ITEM_FRAME.getId(), Material.PAINTING.getId());

    boolean isHanging(int type) {
        return TYPES.contains(type);
    }

    List<HangingChange> plan(List<BlockHistory> history, RollbackKind kind) {
        Map<HangingKey, BlockHistory> decisive = new LinkedHashMap<>();
        Map<HangingKey, List<String>> ids = new HashMap<>();

        for (BlockHistory row : history) {
            HangingKey key = new HangingKey(row.position(), row.type());

            ids.computeIfAbsent(key, _ -> new ArrayList<>()).add(row.id());

            if (kind == RollbackKind.ROLLBACK)
                decisive.putIfAbsent(key, row);
            else
                decisive.put(key, row);
        }

        List<HangingChange> changes = new ArrayList<>(decisive.size());

        decisive.forEach((key, row) -> {
            HangingData data = BlockMetaCodec.decode(row.meta()) instanceof HangingData hanging ? hanging : null;

            boolean placed = row.action() == BlockAction.PLACE.getId();
            boolean spawn = (kind == RollbackKind.ROLLBACK) != placed;

            if (data != null)
                changes.add(HangingChange.builder()
                        .position(key.position())
                        .data(data)
                        .spawn(spawn)
                        .ids(List.copyOf(ids.get(key)))
                        .build());
        });

        return changes;
    }
}
