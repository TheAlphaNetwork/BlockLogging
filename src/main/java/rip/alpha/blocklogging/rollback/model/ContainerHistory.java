package rip.alpha.blocklogging.rollback.model;

import lombok.Builder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.BlockPosition;

@Builder
public record ContainerHistory(String id, BlockPosition position, @Nullable ItemStack item, int amount, int action) {
}
