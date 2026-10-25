package rip.alpha.blocklogging.record.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.capture.HangingDatas;
import rip.alpha.blocklogging.record.model.BlockRecord;
import rip.alpha.blocklogging.record.model.LogBase;
import rip.alpha.blocklogging.record.type.BlockAction;

@RequiredArgsConstructor
public final class HangingListener implements Listener {

    private final RecordService records;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(HangingPlaceEvent event) {

        if (this.records.logging().isHangings())
            log(Actor.player(event.getPlayer()), event.getEntity(), BlockAction.PLACE);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(HangingBreakEvent event) {

        if (!this.records.logging().isHangings())
            return;

        log(remover(event), event.getEntity(), BlockAction.BREAK);
    }

    private Actor remover(HangingBreakEvent event) {

        if (event instanceof HangingBreakByEntityEvent byEntity && byEntity.getRemover() != null)
            return Actor.entity(byEntity.getRemover());

        return Actor.system(event.getCause().name());
    }

    @SuppressWarnings("deprecation")
    private void log(Actor actor, Hanging hanging, BlockAction action) {
        Material type = hanging instanceof ItemFrame ? Material.ITEM_FRAME : Material.PAINTING;

        LogBase base = LogBase.now(actor, BlockPosition.of(hanging.getLocation()));

        this.records.submit(BlockRecord.builder()
                .base(base)
                .type(type.getId())
                .data(0)
                .action(action)
                .meta(HangingDatas.of(hanging))
                .build());
    }
}
