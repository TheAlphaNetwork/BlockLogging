package rip.alpha.blocklogging.record.model;

import org.bukkit.entity.EntityType;

public record SpawnerMeta(EntityType type) implements BlockMeta {
}
