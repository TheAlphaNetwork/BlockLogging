package rip.alpha.blocklogging.record.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.capture.ItemCopies;
import rip.alpha.blocklogging.record.model.ItemRecord;
import rip.alpha.blocklogging.record.model.LogBase;
import rip.alpha.blocklogging.record.type.ItemAction;

@RequiredArgsConstructor
public final class ItemListener implements Listener {

    private final RecordService records;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {

        if (this.records.logging().isItemDrops())
            log(event.getPlayer(), event.getItemDrop().getLocation(), event.getItemDrop().getItemStack(), ItemAction.DROP);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPickup(PlayerPickupItemEvent event) {

        if (this.records.logging().isItemPickups())
            log(event.getPlayer(), event.getItem().getLocation(), event.getItem().getItemStack(), ItemAction.PICKUP);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {

        if (!this.records.logging().isDeathDrops() || event.getKeepInventory())
            return;

        Player player = event.getEntity();

        for (ItemStack item : event.getDrops())
            log(player, player.getLocation(), item, ItemAction.DROP);
    }

    private void log(Player player, Location location, ItemStack item, ItemAction action) {

        if (item == null || item.getType() == Material.AIR)
            return;

        LogBase base = LogBase.now(Actor.player(player), BlockPosition.of(location));

        this.records.submit(new ItemRecord(base, ItemCopies.single(item), item.getAmount(), action));
    }
}
