package rip.alpha.blocklogging.rollback.apply;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.InventoryHolder;
import rip.alpha.blocklogging.rollback.model.BlockTarget;

import java.util.EnumSet;
import java.util.Set;

@UtilityClass
@SuppressWarnings("deprecation")
public class BlockApplier {

    private final Set<Material> CONTAINERS = EnumSet.of(
            Material.CHEST, Material.TRAPPED_CHEST, Material.FURNACE, Material.BURNING_FURNACE, Material.DISPENSER,
            Material.DROPPER, Material.HOPPER, Material.BREWING_STAND, Material.BEACON
    );

    public boolean apply(BlockTarget target) {
        Block block = target.position().block();

        Material material = Material.getMaterial(target.type());

        if (block == null || material == null || !material.isBlock())
            return false;

        if (block.getTypeId() == target.type() && block.getData() == target.data() && target.meta() == null)
            return false;

        if (CONTAINERS.contains(block.getType()))
            clear(block.getState());

        block.setTypeIdAndData(target.type(), (byte) target.data(), false);

        if (target.meta() != null)
            MetaApplier.apply(block, target.meta());

        return true;
    }

    private void clear(BlockState state) {

        switch (state) {
            case Chest chest -> chest.getBlockInventory().clear();
            case InventoryHolder holder -> holder.getInventory().clear();
            default -> {
            }
        }
    }
}
