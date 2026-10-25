package rip.alpha.blocklogging.record.state;

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.capture.ItemCopies;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ContainerViews {

    private final Map<BlockPosition, SharedContainer> containers = new HashMap<>();

    private final Map<UUID, BlockPosition> viewing = new HashMap<>();

    public SharedContainer open(UUID player, BlockPosition position, Inventory inventory) {
        SharedContainer container = this.containers.computeIfAbsent(position, _ -> fresh(position, inventory));

        container.getViewers().add(player);
        this.viewing.put(player, position);

        return container;
    }

    private SharedContainer fresh(BlockPosition position, Inventory inventory) {
        return new SharedContainer(position, inventory, ItemCopies.copy(inventory.getContents()));
    }

    @Nullable
    public SharedContainer viewed(UUID player) {
        BlockPosition position = this.viewing.get(player);

        return position == null ? null : this.containers.get(position);
    }

    @Nullable
    public SharedContainer at(BlockPosition position) {
        return this.containers.get(position);
    }

    public void close(UUID player) {
        BlockPosition position = this.viewing.remove(player);

        SharedContainer container = position == null ? null : this.containers.get(position);

        if (container != null) {
            container.getViewers().remove(player);

            if (container.getViewers().isEmpty())
                this.containers.remove(position);
        }
    }
}
