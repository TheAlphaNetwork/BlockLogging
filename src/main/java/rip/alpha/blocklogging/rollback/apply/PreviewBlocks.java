package rip.alpha.blocklogging.rollback.apply;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.rollback.model.BlockTarget;

import java.util.List;

@UtilityClass
@SuppressWarnings("deprecation")
public class PreviewBlocks {

    public void show(Player player, List<BlockTarget> targets) {

        for (BlockTarget target : targets) {

            Location location = target.position().center();

            Material material = Material.getMaterial(target.type());

            if (location != null && material != null && material.isBlock() && location.getWorld().equals(player.getWorld()))
                player.sendBlockChange(location, material, (byte) target.data());
        }
    }

    public void revert(Player player, List<BlockPosition> positions) {

        for (BlockPosition position : positions) {
            Block block = position.block();

            if (block != null && block.getWorld().equals(player.getWorld()))
                player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
        }
    }
}
