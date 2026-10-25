package rip.alpha.blocklogging.record.model;

import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.record.type.ItemAction;

public record ItemRecord(LogBase base, ItemStack item, int amount, ItemAction action) implements LogRecord {
}
