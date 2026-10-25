package rip.alpha.blocklogging.record.model;

import org.bukkit.entity.EntityType;

public record KillRecord(LogBase base, EntityType type) implements LogRecord {
}
