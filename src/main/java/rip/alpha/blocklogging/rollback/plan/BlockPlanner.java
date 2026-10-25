package rip.alpha.blocklogging.rollback.plan;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.codec.BlockMetaCodec;
import rip.alpha.blocklogging.record.model.BlockMeta;
import rip.alpha.blocklogging.record.model.SignMeta;
import rip.alpha.blocklogging.record.type.BlockAction;
import rip.alpha.blocklogging.rollback.model.BlockHistory;
import rip.alpha.blocklogging.rollback.model.BlockTarget;
import rip.alpha.blocklogging.rollback.type.RollbackKind;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@UtilityClass
@SuppressWarnings("deprecation")
class BlockPlanner {

    private final int AIR = 0;

    private final String LINE_BREAK = "\n";

    private final Set<Integer> SIGNS = Set.of(Material.SIGN_POST.getId(), Material.WALL_SIGN.getId());

    List<BlockTarget> plan(List<BlockHistory> history, RollbackKind kind, Map<BlockPosition, String> signs) {
        Map<BlockPosition, BlockHistory> decisive = new LinkedHashMap<>();
        Map<BlockPosition, List<String>> ids = new HashMap<>();

        for (BlockHistory row : history) {
            ids.computeIfAbsent(row.position(), _ -> new ArrayList<>()).add(row.id());

            if (kind == RollbackKind.ROLLBACK)
                decisive.putIfAbsent(row.position(), row);
            else
                decisive.put(row.position(), row);
        }

        List<BlockTarget> targets = new ArrayList<>(decisive.size());

        for (BlockHistory row : decisive.values()) {
            BlockTarget target = kind == RollbackKind.ROLLBACK ? before(row) : after(row, signs.get(row.position()));

            targets.add(target.toBuilder().ids(List.copyOf(ids.get(row.position()))).build());
        }

        targets.sort(Comparator.comparingInt(target -> target.position().y()));
        return targets;
    }

    boolean isSign(int type) {
        return SIGNS.contains(type);
    }

    private BlockTarget before(BlockHistory row) {

        if (row.action() == BlockAction.PLACE.getId())
            return target(row.position(), AIR, 0, null);

        return target(row.position(), row.type(), row.data(), BlockMetaCodec.decode(row.meta()));
    }

    private BlockTarget after(BlockHistory row, @Nullable String sign) {

        if (row.action() == BlockAction.BREAK.getId())
            return target(row.position(), AIR, 0, null);

        BlockMeta meta = sign != null && isSign(row.type()) ? new SignMeta(sign.split(LINE_BREAK, -1)) : null;

        return target(row.position(), row.type(), row.data(), meta);
    }

    private BlockTarget target(BlockPosition position, int type, int data, @Nullable BlockMeta meta) {
        return BlockTarget.builder()
                .position(position)
                .type(type)
                .data(data)
                .meta(meta)
                .ids(List.of())
                .build();
    }
}
