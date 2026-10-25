package rip.alpha.blocklogging.rollback.model;

import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.common.model.BlockPosition;

public record ContainerChange(BlockPosition position, ItemStack item, int amount, boolean add) implements Change {
}
