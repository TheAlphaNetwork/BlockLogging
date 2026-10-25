package rip.alpha.blocklogging.record.model;

import org.bukkit.inventory.ItemStack;

public record ItemDelta(ItemStack item, int change) {
}
