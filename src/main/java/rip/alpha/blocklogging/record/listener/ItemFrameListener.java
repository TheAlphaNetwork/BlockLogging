package rip.alpha.blocklogging.record.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.capture.ItemCopies;
import rip.alpha.blocklogging.record.model.ContainerRecord;
import rip.alpha.blocklogging.record.model.LogBase;
import rip.alpha.blocklogging.record.type.ContainerAction;

@RequiredArgsConstructor
public final class ItemFrameListener implements Listener {

    private final RecordService records;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInsert(PlayerInteractEntityEvent event) {

        if (!this.records.logging().isHangings() || !(event.getRightClicked() instanceof ItemFrame frame))
            return;

        ItemStack hand = event.getPlayer().getItemInHand();

        if (!empty(frame.getItem()) || empty(hand))
            return;

        log(Actor.player(event.getPlayer()), frame, hand, ContainerAction.ADD);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRemove(EntityDamageByEntityEvent event) {

        if (!this.records.logging().isHangings() || !(event.getEntity() instanceof ItemFrame frame) || empty(frame.getItem()))
            return;

        log(attacker(event.getDamager()), frame, frame.getItem(), ContainerAction.REMOVE);
    }

    private void log(Actor actor, ItemFrame frame, ItemStack item, ContainerAction action) {
        LogBase base = LogBase.now(actor, BlockPosition.of(frame.getLocation()));

        this.records.submit(ContainerRecord.builder()
                .base(base)
                .item(ItemCopies.single(item))
                .amount(1)
                .action(action)
                .build());
    }

    private Actor attacker(Entity damager) {

        if (damager instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter)
            return Actor.entity(shooter);

        return Actor.entity(damager);
    }

    private boolean empty(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }
}
