package rip.alpha.blocklogging.record.capture;

import lombok.experimental.UtilityClass;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.BlockPosition;

@UtilityClass
public class ContainerLocations {

    @Nullable
    public BlockPosition of(@Nullable InventoryHolder holder) {
        return switch (holder) {
            case DoubleChest chest when chest.getLeftSide() instanceof BlockState left -> BlockPosition.of(left.getBlock());
            case BlockState state -> BlockPosition.of(state.getBlock());
            case null, default -> null;
        };
    }

    @Nullable
    public BlockPosition of(Block block) {
        BlockState state = block.getState();

        if (state instanceof Chest chest)
            return of(chest.getInventory().getHolder());

        return state instanceof InventoryHolder holder ? of(holder) : null;
    }
}
