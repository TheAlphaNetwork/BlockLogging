package rip.alpha.blocklogging.record.model;

import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.common.model.BlockPosition;

public record ContainerSession(BlockPosition position, ItemStack[] snapshot) {
}
