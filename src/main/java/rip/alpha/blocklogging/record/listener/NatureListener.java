package rip.alpha.blocklogging.record.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.world.StructureGrowEvent;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.capture.BlockCapture;
import rip.alpha.blocklogging.record.capture.Causes;

@RequiredArgsConstructor
public final class NatureListener implements Listener {

    private final RecordService records;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDecay(LeavesDecayEvent event) {

        if (this.records.logging().isLeavesDecay())
            this.records.submit(BlockCapture.broke(Causes.DECAY, event.getBlock()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGrow(StructureGrowEvent event) {

        if (!this.records.logging().isTreeGrowth())
            return;

        Actor actor = event.getPlayer() == null ? Causes.TREE : Actor.player(event.getPlayer());

        for (BlockState state : event.getBlocks()) {
            Block current = state.getBlock();

            if (current.getType() != Material.AIR)
                this.records.submit(BlockCapture.broke(actor, current));

            this.records.submit(BlockCapture.placed(actor, state));
        }
    }
}
