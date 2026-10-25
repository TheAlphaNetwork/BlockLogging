package rip.alpha.blocklogging.record.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.capture.Causes;
import rip.alpha.blocklogging.record.capture.ContainerLocations;
import rip.alpha.blocklogging.record.capture.ItemCopies;
import rip.alpha.blocklogging.record.capture.ItemDiff;
import rip.alpha.blocklogging.record.model.ContainerRecord;
import rip.alpha.blocklogging.record.model.ContainerSession;
import rip.alpha.blocklogging.record.model.ItemDelta;
import rip.alpha.blocklogging.record.model.LogBase;
import rip.alpha.blocklogging.record.state.OpenContainers;
import rip.alpha.blocklogging.record.type.ContainerAction;

@RequiredArgsConstructor
public final class ContainerListener implements Listener {

    private final RecordService records;

    private final OpenContainers open;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onOpen(InventoryOpenEvent event) {

        if (!this.records.logging().isContainers() || !(event.getPlayer() instanceof Player player))
            return;

        Inventory top = event.getInventory();

        BlockPosition position = ContainerLocations.of(top.getHolder());

        if (position != null)
            this.open.open(player.getUniqueId(), new ContainerSession(position, ItemCopies.copy(top.getContents())));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClose(InventoryCloseEvent event) {
        ContainerSession session = this.open.close(event.getPlayer().getUniqueId());

        if (session == null || !(event.getPlayer() instanceof Player player))
            return;

        Actor actor = Actor.player(player);

        for (ItemDelta delta : ItemDiff.between(session.snapshot(), event.getInventory().getContents()))
            log(actor, session.position(), delta.item(), delta.change());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(InventoryMoveItemEvent event) {

        if (!this.records.logging().isHopperTransactions())
            return;

        ItemStack item = ItemCopies.single(event.getItem());

        int amount = event.getItem().getAmount();

        BlockPosition source = ContainerLocations.of(event.getSource().getHolder());
        BlockPosition destination = ContainerLocations.of(event.getDestination().getHolder());

        if (source != null)
            log(Causes.HOPPER, source, item, -amount);

        if (destination != null)
            log(Causes.HOPPER, destination, item, amount);
    }

    private void log(Actor actor, BlockPosition position, ItemStack item, int change) {
        ContainerAction action = change > 0 ? ContainerAction.ADD : ContainerAction.REMOVE;

        this.records.submit(ContainerRecord.builder()
                .base(LogBase.now(actor, position))
                .item(item)
                .amount(Math.abs(change))
                .action(action)
                .build());
    }
}
