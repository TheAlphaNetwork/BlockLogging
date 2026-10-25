package rip.alpha.blocklogging.record.capture;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Bed;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;

@UtilityClass
public class MultiBlocks {

    private final int UPPER_HALF = 8;
    private final Set<Material> TALL = EnumSet.of(
            Material.WOODEN_DOOR, Material.IRON_DOOR_BLOCK, Material.SPRUCE_DOOR, Material.BIRCH_DOOR,
            Material.JUNGLE_DOOR, Material.ACACIA_DOOR, Material.DARK_OAK_DOOR, Material.DOUBLE_PLANT
    );

    @Nullable
    @SuppressWarnings("deprecation")
    public Block otherHalf(Block block) {
        Material type = block.getType();

        if (TALL.contains(type)) {
            Block other = block.getRelative((block.getData() & UPPER_HALF) != 0 ? BlockFace.DOWN : BlockFace.UP);

            return other.getType() == type ? other : null;
        }

        if (type != Material.BED_BLOCK)
            return null;

        Bed bed = new Bed(type, block.getData());

        Block other = block.getRelative(bed.isHeadOfBed() ? bed.getFacing().getOppositeFace() : bed.getFacing());

        return other.getType() == type ? other : null;
    }
}
