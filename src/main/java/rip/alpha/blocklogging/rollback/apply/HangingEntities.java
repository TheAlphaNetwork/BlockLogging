package rip.alpha.blocklogging.rollback.apply;

import lombok.experimental.UtilityClass;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.BlockPosition;

@UtilityClass
class HangingEntities {

    @Nullable
    <T extends Hanging> T find(BlockPosition position, Class<T> type) {
        Block block = position.block();

        if (block == null)
            return null;

        for (Entity entity : block.getChunk().getEntities()) {

            if (type.isInstance(entity) && BlockPosition.of(entity.getLocation()).equals(position))
                return type.cast(entity);
        }

        return null;
    }
}
