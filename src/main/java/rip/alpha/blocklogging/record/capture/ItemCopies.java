package rip.alpha.blocklogging.record.capture;

import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class ItemCopies {

    public ItemStack[] copy(ItemStack[] contents) {
        ItemStack[] copy = new ItemStack[contents.length];

        for (int index = 0; index < contents.length; index++)
            copy[index] = contents[index] == null ? null : contents[index].clone();

        return copy;
    }

    public ItemStack single(ItemStack item) {
        ItemStack copy = item.clone();

        copy.setAmount(1);
        return copy;
    }
}
