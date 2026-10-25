package rip.alpha.blocklogging.record.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.capture.BlockCapture;
import rip.alpha.blocklogging.record.model.BlockRecord;
import rip.alpha.blocklogging.record.model.LogBase;
import rip.alpha.blocklogging.record.type.BlockAction;

@RequiredArgsConstructor
public final class EntityBlockListener implements Listener {

    private final RecordService records;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @SuppressWarnings("deprecation")
    public void onChange(EntityChangeBlockEvent event) {

        if (!this.records.logging().isEntityChanges())
            return;

        Actor actor = Actor.entity(event.getEntity());

        Block block = event.getBlock();

        if (block.getType() != Material.AIR)
            this.records.submit(BlockCapture.broke(actor, block));

        if (event.getTo() == Material.AIR)
            return;

        LogBase base = LogBase.now(actor, BlockPosition.of(block));

        this.records.submit(BlockRecord.builder()
                .base(base)
                .type(event.getTo().getId())
                .data(event.getData())
                .action(BlockAction.PLACE)
                .meta(null)
                .build());
    }
}
