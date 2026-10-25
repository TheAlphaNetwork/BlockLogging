package rip.alpha.blocklogging.rollback.model;

import lombok.Builder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Builder
public record ItemHistory(String id, @Nullable UUID player, @Nullable ItemStack item, int amount, int action) {
}
