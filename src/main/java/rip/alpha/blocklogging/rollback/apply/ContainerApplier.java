package rip.alpha.blocklogging.rollback.apply;

import lombok.experimental.UtilityClass;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.rollback.model.ContainerChange;

@UtilityClass
public class ContainerApplier {

    public boolean apply(ContainerChange change) {
        Block block = change.position().block();

        if (block == null)
            return false;

        if (!(block.getState() instanceof InventoryHolder holder))
            return FrameContents.apply(change);

        Inventory inventory = holder.getInventory();

        int stack = Math.max(1, change.item().getMaxStackSize());
        int remaining = change.amount();

        while (remaining > 0) {
            ItemStack item = change.item().clone();

            item.setAmount(Math.min(remaining, stack));

            if (change.add())
                inventory.addItem(item);
            else
                inventory.removeItem(item);

            remaining -= item.getAmount();
        }

        return true;
    }
}
