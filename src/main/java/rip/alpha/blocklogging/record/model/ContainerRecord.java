package rip.alpha.blocklogging.record.model;

import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.record.type.ContainerAction;

public record ContainerRecord(LogBase base, ItemStack item, int amount, ContainerAction action) implements LogRecord {
}
