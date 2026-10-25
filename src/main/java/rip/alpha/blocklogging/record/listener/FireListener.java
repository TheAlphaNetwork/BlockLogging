package rip.alpha.blocklogging.record.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.capture.BlockCapture;
import rip.alpha.blocklogging.record.capture.Causes;
import rip.alpha.blocklogging.record.type.BlockAction;

@RequiredArgsConstructor
public final class FireListener implements Listener {

    private final RecordService records;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBurn(BlockBurnEvent event) {

        if (this.records.logging().isFire())
            this.records.submit(BlockCapture.broke(Causes.FIRE, event.getBlock()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @SuppressWarnings("deprecation")
    public void onIgnite(BlockIgniteEvent event) {

        if (!this.records.logging().isFire() || event.getPlayer() == null)
            return;

        BlockPosition position = BlockPosition.of(event.getBlock());

        this.records.submit(BlockCapture.raw(Actor.player(event.getPlayer()), position, Material.FIRE.getId(), BlockAction.PLACE));
    }
}
