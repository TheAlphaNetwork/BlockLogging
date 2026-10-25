package rip.alpha.blocklogging.record.capture;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.record.model.ItemDelta;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ItemDiff {

    public List<ItemDelta> between(ItemStack[] before, ItemStack[] after) {
        List<ItemStack> keys = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();

        count(before, keys, counts, -1);
        count(after, keys, counts, 1);

        List<ItemDelta> deltas = new ArrayList<>();

        for (int index = 0; index < keys.size(); index++) {

            if (counts.get(index) != 0)
                deltas.add(new ItemDelta(keys.get(index), counts.get(index)));
        }

        return deltas;
    }

    private void count(ItemStack[] contents, List<ItemStack> keys, List<Integer> counts, int sign) {

        for (ItemStack item : contents) {

            if (item == null || item.getType() == Material.AIR)
                continue;

            int index = indexOf(keys, item);

            if (index < 0) {
                keys.add(ItemCopies.single(item));
                counts.add(0);

                index = keys.size() - 1;
            }

            counts.set(index, counts.get(index) + sign * item.getAmount());
        }
    }

    private int indexOf(List<ItemStack> keys, ItemStack item) {

        for (int index = 0; index < keys.size(); index++) {

            if (keys.get(index).isSimilar(item))
                return index;
        }

        return -1;
    }
}
