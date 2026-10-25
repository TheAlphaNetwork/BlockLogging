package rip.alpha.blocklogging.query.render;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@UtilityClass
class BlockItems {

    private final Map<Material, Material> ITEMS = new EnumMap<>(Material.class);
    private final Set<Material> VARIANTS = EnumSet.of(
            Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE, Material.CARPET,
            Material.WOOD, Material.LOG, Material.LOG_2, Material.LEAVES, Material.LEAVES_2, Material.STONE, Material.SAND,
            Material.SANDSTONE, Material.RED_SANDSTONE, Material.SMOOTH_BRICK, Material.QUARTZ_BLOCK, Material.PRISMARINE,
            Material.INK_SACK, Material.SAPLING, Material.RED_ROSE, Material.DOUBLE_PLANT, Material.LONG_GRASS, Material.DIRT
    );

    static {
        put(Material.WALL_SIGN, Material.SIGN);
        put(Material.SIGN_POST, Material.SIGN);
        put(Material.WOODEN_DOOR, Material.WOOD_DOOR);
        put(Material.IRON_DOOR_BLOCK, Material.IRON_DOOR);
        put(Material.SPRUCE_DOOR, Material.SPRUCE_DOOR_ITEM);
        put(Material.BIRCH_DOOR, Material.BIRCH_DOOR_ITEM);
        put(Material.JUNGLE_DOOR, Material.JUNGLE_DOOR_ITEM);
        put(Material.ACACIA_DOOR, Material.ACACIA_DOOR_ITEM);
        put(Material.DARK_OAK_DOOR, Material.DARK_OAK_DOOR_ITEM);
        put(Material.REDSTONE_WIRE, Material.REDSTONE);
        put(Material.CROPS, Material.WHEAT);
        put(Material.SUGAR_CANE_BLOCK, Material.SUGAR_CANE);
        put(Material.BED_BLOCK, Material.BED);
        put(Material.CAKE_BLOCK, Material.CAKE);
        put(Material.BREWING_STAND, Material.BREWING_STAND_ITEM);
        put(Material.CAULDRON, Material.CAULDRON_ITEM);
        put(Material.FLOWER_POT, Material.FLOWER_POT_ITEM);
        put(Material.SKULL, Material.SKULL_ITEM);
        put(Material.DIODE_BLOCK_OFF, Material.DIODE);
        put(Material.DIODE_BLOCK_ON, Material.DIODE);
        put(Material.REDSTONE_COMPARATOR_OFF, Material.REDSTONE_COMPARATOR);
        put(Material.REDSTONE_COMPARATOR_ON, Material.REDSTONE_COMPARATOR);
        put(Material.WATER, Material.WATER_BUCKET);
        put(Material.STATIONARY_WATER, Material.WATER_BUCKET);
        put(Material.LAVA, Material.LAVA_BUCKET);
        put(Material.STATIONARY_LAVA, Material.LAVA_BUCKET);
        put(Material.FIRE, Material.FLINT_AND_STEEL);
        put(Material.PISTON_EXTENSION, Material.PISTON_BASE);
        put(Material.PISTON_MOVING_PIECE, Material.PISTON_BASE);
        put(Material.STANDING_BANNER, Material.BANNER);
        put(Material.WALL_BANNER, Material.BANNER);
        put(Material.NETHER_WARTS, Material.NETHER_STALK);
        put(Material.CARROT, Material.CARROT_ITEM);
        put(Material.POTATO, Material.POTATO_ITEM);
        put(Material.TRIPWIRE, Material.STRING);
        put(Material.REDSTONE_TORCH_OFF, Material.REDSTONE_TORCH_ON);
        put(Material.BURNING_FURNACE, Material.FURNACE);
        put(Material.GLOWING_REDSTONE_ORE, Material.REDSTONE_ORE);
        put(Material.DAYLIGHT_DETECTOR_INVERTED, Material.DAYLIGHT_DETECTOR);
        put(Material.REDSTONE_LAMP_ON, Material.REDSTONE_LAMP_OFF);
        put(Material.PUMPKIN_STEM, Material.PUMPKIN_SEEDS);
        put(Material.MELON_STEM, Material.MELON_SEEDS);
        put(Material.COCOA, Material.INK_SACK);
        put(Material.AIR, Material.BARRIER);
    }

    @Nullable
    Material item(@Nullable Material block) {
        return block == null ? null : ITEMS.getOrDefault(block, block);
    }

    boolean keepsData(Material material) {
        return VARIANTS.contains(material) || !material.isBlock();
    }

    private void put(Material block, Material item) {
        ITEMS.put(block, item);
    }
}
