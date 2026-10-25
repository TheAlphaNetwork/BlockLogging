package rip.alpha.blocklogging.record.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.capture.BlockCapture;
import rip.alpha.blocklogging.record.capture.Causes;
import rip.alpha.blocklogging.record.state.LiquidOwners;
import rip.alpha.blocklogging.record.type.BlockAction;

import java.util.EnumSet;
import java.util.Set;

@RequiredArgsConstructor
public final class LiquidFlowListener implements Listener {

    private static final Set<Material> FORMED = EnumSet.of(Material.COBBLESTONE, Material.STONE, Material.OBSIDIAN);

    private final RecordService records;

    private final LiquidOwners owners;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @SuppressWarnings("deprecation")
    public void onFlow(BlockFromToEvent event) {
        Block from = event.getBlock();

        if (!this.records.logging().isLiquidFlow() || !from.isLiquid())
            return;

        Block to = event.getToBlock();

        Actor owner = this.owners.get(BlockPosition.of(from));

        boolean destroys = to.getType() != Material.AIR && !to.isLiquid();

        if (owner == null && !destroys)
            return;

        boolean lava = from.getType() == Material.LAVA || from.getType() == Material.STATIONARY_LAVA;

        Actor actor = owner != null ? owner : lava ? Causes.LAVA : Causes.WATER;

        if (destroys)
            this.records.submit(BlockCapture.broke(actor, to));

        if (owner == null)
            return;

        BlockPosition target = BlockPosition.of(to);

        this.records.submit(BlockCapture.raw(actor, target, (lava ? Material.LAVA : Material.WATER).getId(), BlockAction.PLACE));
        this.owners.put(target, owner);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onForm(BlockFormEvent event) {

        if (!this.records.logging().isLiquidFlow() || !FORMED.contains(event.getNewState().getType()))
            return;

        Actor owner = this.owners.get(BlockPosition.of(event.getBlock()));

        if (owner != null)
            this.records.submit(BlockCapture.placed(owner, event.getNewState()));
    }
}
