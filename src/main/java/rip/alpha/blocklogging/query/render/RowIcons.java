package rip.alpha.blocklogging.query.render;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import rip.alpha.aspigot.item.Items;
import rip.alpha.blocklogging.query.model.LookupRow;

@UtilityClass
@SuppressWarnings("deprecation")
public class RowIcons {

    private final int MAX_STACK = 64;

    public ItemStack of(LookupRow row) {
        return switch (row.table()) {
            case BLOCK, CONTAINER, ITEM -> block(row);
            case KILL -> new ItemStack(Material.MONSTER_EGG, 1, (short) row.type());
            case SIGN -> new ItemStack(Material.SIGN);
            case CHAT -> new ItemStack(Material.PAPER);
            case COMMAND -> new ItemStack(Material.COMMAND);
            case SESSION -> new ItemStack(row.action() == 1 ? Material.EMERALD : Material.REDSTONE);
            case USERNAME -> new ItemStack(Material.NAME_TAG);
        };
    }

    private ItemStack block(LookupRow row) {
        Material material = BlockItems.item(Material.getMaterial(row.type()));

        if (!Items.isItem(material))
            return new ItemStack(Material.BARRIER);

        int amount = Math.clamp(row.amount(), 1, MAX_STACK);

        short data = BlockItems.keepsData(material) ? (short) row.data() : 0;

        return new ItemStack(material, amount, data);
    }
}
