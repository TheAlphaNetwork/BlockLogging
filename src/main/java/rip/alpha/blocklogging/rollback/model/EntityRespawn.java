package rip.alpha.blocklogging.rollback.model;

import org.bukkit.entity.EntityType;
import rip.alpha.blocklogging.common.model.BlockPosition;

public record EntityRespawn(BlockPosition position, EntityType type) implements Change {
}
