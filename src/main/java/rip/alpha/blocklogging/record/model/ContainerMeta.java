package rip.alpha.blocklogging.record.model;

import org.bukkit.inventory.ItemStack;

public record ContainerMeta(ItemStack[] contents) implements BlockMeta {
}
