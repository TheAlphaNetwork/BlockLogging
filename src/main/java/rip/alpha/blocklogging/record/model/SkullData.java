package rip.alpha.blocklogging.record.model;

import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Nullable;

public record SkullData(SkullType type, @Nullable String owner, BlockFace rotation) implements BlockMeta {
}
