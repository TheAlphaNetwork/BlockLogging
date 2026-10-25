package rip.alpha.blocklogging.rollback.model;

import lombok.Builder;
import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.List;

@Builder
public record ContainerChange(BlockPosition position, ItemStack item, int amount, boolean add, String id) implements Change {

    @Override
    public LogTable table() {
        return LogTable.CONTAINER;
    }

    @Override
    public List<String> ids() {
        return List.of(this.id);
    }
}
