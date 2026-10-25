package rip.alpha.blocklogging.record.model;

import lombok.Builder;
import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.record.type.ContainerAction;

@Builder
public record ContainerRecord(LogBase base, ItemStack item, int amount, ContainerAction action) implements LogRecord {
}
