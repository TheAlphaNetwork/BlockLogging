package rip.alpha.blocklogging.record.capture;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;

import java.util.EnumSet;
import java.util.Set;

@UtilityClass
public class Interactables {

    private final Set<Material> TYPES = EnumSet.of(
            Material.LEVER, Material.STONE_BUTTON, Material.WOOD_BUTTON, Material.WOODEN_DOOR, Material.SPRUCE_DOOR,
            Material.BIRCH_DOOR, Material.JUNGLE_DOOR, Material.ACACIA_DOOR, Material.DARK_OAK_DOOR, Material.TRAP_DOOR,
            Material.FENCE_GATE, Material.SPRUCE_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.JUNGLE_FENCE_GATE,
            Material.DARK_OAK_FENCE_GATE, Material.ACACIA_FENCE_GATE, Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON,
            Material.REDSTONE_COMPARATOR_OFF, Material.REDSTONE_COMPARATOR_ON, Material.NOTE_BLOCK, Material.CAKE_BLOCK,
            Material.DRAGON_EGG, Material.JUKEBOX
    );

    public boolean is(Material type) {
        return TYPES.contains(type);
    }
}
