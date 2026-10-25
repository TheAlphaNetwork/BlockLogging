package rip.alpha.blocklogging.record.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.capture.Causes;
import rip.alpha.blocklogging.record.capture.ContainerLocations;
import rip.alpha.blocklogging.record.capture.ContainerTracker;
import rip.alpha.blocklogging.record.capture.ItemCopies;
import rip.alpha.blocklogging.record.state.SharedContainer;

@RequiredArgsConstructor
public final class ContainerListener implements Listener {

    private final RecordService records;

    private final ContainerTracker tracker;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onOpen(InventoryOpenEvent event) {

        if (!this.records.logging().isContainers() || !(event.getPlayer() instanceof Player player))
            return;

        Inventory top = event.getInventory();

        BlockPosition position = ContainerLocations.of(top.getHolder());

        if (position != null)
            this.tracker.getViews().open(player.getUniqueId(), position, top);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        touched(event.getWhoClicked());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        touched(event.getWhoClicked());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClose(InventoryCloseEvent event) {
        this.tracker.close(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(InventoryMoveItemEvent event) {
        BlockPosition source = ContainerLocations.of(event.getSource().getHolder());
        BlockPosition destination = ContainerLocations.of(event.getDestination().getHolder());

        absorb(source);
        absorb(destination);

        if (!this.records.logging().isHopperTransactions())
            return;

        ItemStack item = ItemCopies.single(event.getItem());

        int amount = event.getItem().getAmount();

        if (source != null)
            this.tracker.log(Causes.HOPPER, source, item, -amount);

        if (destination != null)
            this.tracker.log(Causes.HOPPER, destination, item, amount);
    }

    private void touched(HumanEntity viewer) {
        SharedContainer container = this.tracker.getViews().viewed(viewer.getUniqueId());

        if (container != null && viewer instanceof Player player)
            this.tracker.touched(container, Actor.player(player));
    }

    private void absorb(@Nullable BlockPosition position) {
        SharedContainer container = position == null ? null : this.tracker.getViews().at(position);

        if (container != null)
            this.tracker.touched(container, null);
    }
}
