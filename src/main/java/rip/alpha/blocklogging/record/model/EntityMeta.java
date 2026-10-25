package rip.alpha.blocklogging.record.model;

import lombok.Builder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Builder
public record EntityMeta(@Nullable String name, boolean baby, Map<String, String> traits, ItemStack[] equipment) {
}
