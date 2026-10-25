package rip.alpha.blocklogging.rollback.model;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.BlockPosition;

public record ContainerHistory(long id, BlockPosition position, @Nullable ItemStack item, int amount, int action) {
}
