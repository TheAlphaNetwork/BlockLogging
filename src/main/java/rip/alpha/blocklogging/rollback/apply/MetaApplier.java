package rip.alpha.blocklogging.rollback.apply;

import lombok.experimental.UtilityClass;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.record.model.BannerData;
import rip.alpha.blocklogging.record.model.BlockMeta;
import rip.alpha.blocklogging.record.model.ContainerMeta;
import rip.alpha.blocklogging.record.model.SignMeta;
import rip.alpha.blocklogging.record.model.SkullData;
import rip.alpha.blocklogging.record.model.SpawnerMeta;

import java.util.Arrays;

@UtilityClass
class MetaApplier {

    void apply(Block block, BlockMeta meta) {
        BlockState state = block.getState();

        switch (meta) {
            case ContainerMeta container -> fill(state, container.contents());
            case SignMeta sign when state instanceof Sign target -> sign(target, sign.lines());

            case SpawnerMeta spawner when state instanceof CreatureSpawner target -> {
                target.setSpawnedType(spawner.type());
                target.update(true, false);
            }

            case SkullData skull when state instanceof Skull target -> skull(target, skull);

            case BannerData banner when state instanceof Banner target -> {
                target.setBaseColor(banner.base());
                target.setPatterns(banner.patterns());
                target.update(true, false);
            }
            default -> {
            }
        }
    }

    private void fill(BlockState state, ItemStack[] contents) {
        Inventory inventory = switch (state) {
            case Chest chest -> chest.getBlockInventory();
            case InventoryHolder holder -> holder.getInventory();
            default -> null;
        };

        if (inventory != null)
            inventory.setContents(Arrays.copyOf(contents, inventory.getSize()));
    }

    private void sign(Sign target, String[] lines) {

        for (int index = 0; index < Math.min(lines.length, 4); index++)
            target.setLine(index, lines[index]);

        target.update(true, false);
    }

    private void skull(Skull target, SkullData skull) {
        target.setSkullType(skull.type());
        target.setRotation(skull.rotation());

        if (skull.owner() != null)
            target.setOwner(skull.owner());

        target.update(true, false);
    }
}
