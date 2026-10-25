package rip.alpha.blocklogging.record.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.capture.Causes;
import rip.alpha.blocklogging.record.capture.EntityTraits;
import rip.alpha.blocklogging.record.model.KillRecord;
import rip.alpha.blocklogging.record.model.LogBase;

@RequiredArgsConstructor
public final class KillListener implements Listener {

    private final RecordService records;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(EntityDeathEvent event) {

        if (!this.records.logging().isEntityKills())
            return;

        LivingEntity entity = event.getEntity();

        LogBase base = LogBase.now(Causes.killer(entity), BlockPosition.of(entity.getLocation()));

        this.records.submit(new KillRecord(base, entity.getType(), EntityTraits.capture(entity)));
    }
}
