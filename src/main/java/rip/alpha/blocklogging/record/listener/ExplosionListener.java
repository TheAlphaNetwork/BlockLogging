package rip.alpha.blocklogging.record.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.capture.BlockCapture;
import rip.alpha.blocklogging.record.capture.Causes;

@RequiredArgsConstructor
public final class ExplosionListener implements Listener {

    private final RecordService records;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {

        if (!this.records.logging().isExplosions() || event.blockList().isEmpty())
            return;

        Actor actor = Causes.explosion(event.getEntity());

        for (Block block : event.blockList()) {

            if (block.getType() != Material.AIR)
                this.records.submit(BlockCapture.broke(actor, block));
        }
    }
}
