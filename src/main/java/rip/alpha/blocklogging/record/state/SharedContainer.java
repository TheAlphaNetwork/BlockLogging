package rip.alpha.blocklogging.record.state;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.common.model.BlockPosition;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public final class SharedContainer {

    private final BlockPosition position;

    private final Inventory inventory;

    private final Set<UUID> viewers = new HashSet<>();

    private ItemStack[] snapshot;

    @Nullable
    private Actor changedBy;

    private boolean dirty;
    private boolean scheduled;

    public SharedContainer(BlockPosition position, Inventory inventory, ItemStack[] snapshot) {
        this.position = position;
        this.inventory = inventory;
        this.snapshot = snapshot;
    }
}
