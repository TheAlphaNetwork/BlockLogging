package rip.alpha.blocklogging.rollback.model;

import lombok.Builder;
import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.List;
import java.util.UUID;

@Builder
public record ItemChange(UUID player, ItemStack item, int amount, boolean give, String id) implements Change {

    @Override
    public LogTable table() {
        return LogTable.ITEM;
    }

    @Override
    public List<String> ids() {
        return List.of(this.id);
    }
}
