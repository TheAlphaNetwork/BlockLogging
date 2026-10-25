package rip.alpha.blocklogging.record.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PistonBaseMaterial;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.capture.BlockCapture;
import rip.alpha.blocklogging.record.capture.Causes;

import java.util.List;

@RequiredArgsConstructor
public final class PistonListener implements Listener {

    private final RecordService records;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExtend(BlockPistonExtendEvent event) {
        move(event.getBlock(), event.getBlocks(), false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRetract(BlockPistonRetractEvent event) {
        move(event.getBlock(), event.getBlocks(), true);
    }

    @SuppressWarnings("deprecation")
    private void move(Block piston, List<Block> blocks, boolean retract) {

        if (!this.records.logging().isPistons() || blocks.isEmpty())
            return;

        MaterialData data = piston.getType().getNewData(piston.getData());

        if (!(data instanceof PistonBaseMaterial base))
            return;

        BlockFace direction = retract ? base.getFacing().getOppositeFace() : base.getFacing();

        for (Block block : blocks) {

            if (block.getType() != Material.AIR)
                this.records.submit(BlockCapture.broke(Causes.PISTON, block));
        }

        for (Block block : blocks)
            this.records.submit(BlockCapture.moved(Causes.PISTON, block, block.getRelative(direction)));
    }
}
