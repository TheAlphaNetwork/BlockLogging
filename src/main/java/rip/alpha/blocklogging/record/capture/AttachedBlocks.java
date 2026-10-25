package rip.alpha.blocklogging.record.capture;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@UtilityClass
@SuppressWarnings("deprecation")
public class AttachedBlocks {

    private final List<BlockFace> FACES = List.of(BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);
    private final Set<Material> SUPPORTED = EnumSet.of(
            Material.RED_ROSE, Material.YELLOW_FLOWER, Material.LONG_GRASS, Material.DEAD_BUSH, Material.SAPLING,
            Material.RED_MUSHROOM, Material.BROWN_MUSHROOM, Material.CROPS, Material.CARROT, Material.POTATO,
            Material.NETHER_WARTS, Material.REDSTONE_WIRE, Material.RAILS, Material.POWERED_RAIL, Material.DETECTOR_RAIL,
            Material.ACTIVATOR_RAIL, Material.CARPET, Material.SNOW, Material.STONE_PLATE, Material.WOOD_PLATE,
            Material.GOLD_PLATE, Material.IRON_PLATE, Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON,
            Material.REDSTONE_COMPARATOR_OFF, Material.REDSTONE_COMPARATOR_ON, Material.CACTUS, Material.SUGAR_CANE_BLOCK,
            Material.DOUBLE_PLANT, Material.FLOWER_POT, Material.STANDING_BANNER, Material.WOODEN_DOOR, Material.IRON_DOOR_BLOCK,
            Material.SPRUCE_DOOR, Material.BIRCH_DOOR, Material.JUNGLE_DOOR, Material.ACACIA_DOOR, Material.DARK_OAK_DOOR
    );

    private final Set<Material> ATTACHABLE = EnumSet.of(
            Material.TORCH, Material.REDSTONE_TORCH_ON, Material.REDSTONE_TORCH_OFF, Material.WALL_SIGN, Material.SIGN_POST,
            Material.LADDER, Material.STONE_BUTTON, Material.WOOD_BUTTON, Material.LEVER, Material.TRAP_DOOR,
            Material.IRON_TRAPDOOR, Material.TRIPWIRE_HOOK, Material.WALL_BANNER
    );

    public List<Block> of(Block block) {
        List<Block> attached = new ArrayList<>();

        for (BlockFace face : FACES) {
            Block relative = block.getRelative(face);

            if (face == BlockFace.UP && SUPPORTED.contains(relative.getType()))
                attached.add(relative);
            else if (attachedTo(relative, block))
                attached.add(relative);
        }

        Block other = MultiBlocks.otherHalf(block);

        if (other != null && !attached.contains(other))
            attached.add(other);

        return attached;
    }

    private boolean attachedTo(Block candidate, Block support) {

        if (!ATTACHABLE.contains(candidate.getType()))
            return false;

        MaterialData data = candidate.getType().getNewData(candidate.getData());

        return data instanceof Attachable attachable && candidate.getRelative(attachable.getAttachedFace()).equals(support);
    }
}
