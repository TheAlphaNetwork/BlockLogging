package rip.alpha.blocklogging.record.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.capture.AttachedBlocks;
import rip.alpha.blocklogging.record.capture.BlockCapture;

@RequiredArgsConstructor
public final class BlockBreakListener implements Listener {

    private final RecordService records;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {

        if (!this.records.logging().isBlockBreak())
            return;

        Actor actor = Actor.player(event.getPlayer());

        for (Block attached : AttachedBlocks.of(event.getBlock()))
            this.records.submit(BlockCapture.broke(actor, attached));

        this.records.submit(BlockCapture.broke(actor, event.getBlock()));
    }
}
