package rip.alpha.blocklogging.rollback.apply;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.rollback.model.ItemChange;

@UtilityClass
public class ItemApplier {

    public boolean apply(ItemChange change) {
        Player player = Bukkit.getPlayer(change.player());

        if (player == null)
            return false;

        int stack = Math.max(1, change.item().getMaxStackSize());
        int remaining = change.amount();

        while (remaining > 0) {
            ItemStack item = change.item().clone();

            item.setAmount(Math.min(remaining, stack));
            remaining -= item.getAmount();

            if (change.give())
                give(player, item);
            else
                player.getInventory().removeItem(item);
        }

        return true;
    }

    private void give(Player player, ItemStack item) {

        for (ItemStack overflow : player.getInventory().addItem(item).values())
            player.getWorld().dropItemNaturally(player.getLocation(), overflow);
    }
}
