package rip.alpha.blocklogging.record.model;

import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

public record KillRecord(LogBase base, EntityType type, @Nullable EntityMeta meta) implements LogRecord {
}
