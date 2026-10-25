package rip.alpha.blocklogging.record.capture;

import lombok.experimental.UtilityClass;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.record.model.BannerData;
import rip.alpha.blocklogging.record.model.BlockMeta;
import rip.alpha.blocklogging.record.model.ContainerMeta;
import rip.alpha.blocklogging.record.model.SignMeta;
import rip.alpha.blocklogging.record.model.SkullData;
import rip.alpha.blocklogging.record.model.SpawnerMeta;

import java.util.List;

@UtilityClass
public class BlockMetas {

    @Nullable
    public BlockMeta of(BlockState state) {
        return switch (state) {
            case Chest chest -> new ContainerMeta(ItemCopies.copy(chest.getBlockInventory().getContents()));
            case InventoryHolder holder -> new ContainerMeta(ItemCopies.copy(holder.getInventory().getContents()));
            case Sign sign -> new SignMeta(sign.getLines().clone());
            case CreatureSpawner spawner -> new SpawnerMeta(spawner.getSpawnedType());
            case Skull skull -> new SkullData(skull.getSkullType(), skull.hasOwner() ? skull.getOwner() : null, skull.getRotation());
            case Banner banner -> new BannerData(banner.getBaseColor(), List.copyOf(banner.getPatterns()));
            default -> null;
        };
    }
}
