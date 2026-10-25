package rip.alpha.blocklogging.record.model;

import lombok.Builder;
import org.bukkit.Art;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Builder
public record HangingData(EntityType type, BlockFace facing, @Nullable ItemStack item, @Nullable Rotation rotation, @Nullable Art art)
        implements BlockMeta {
}
