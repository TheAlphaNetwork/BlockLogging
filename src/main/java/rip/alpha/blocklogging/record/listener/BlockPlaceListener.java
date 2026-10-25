package rip.alpha.blocklogging.record.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.capture.BlockCapture;
import rip.alpha.blocklogging.record.state.PositionOwners;

import java.util.List;

@RequiredArgsConstructor
public final class BlockPlaceListener implements Listener {

    private final RecordService records;

    private final PositionOwners tnt;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {

        if (!this.records.logging().isBlockPlace())
            return;

        Actor actor = Actor.player(event.getPlayer());

        for (BlockState state : replaced(event)) {

            if (state.getType() != Material.AIR)
                this.records.submit(BlockCapture.broke(actor, state));

            this.records.submit(BlockCapture.placed(actor, state.getBlock()));
        }

        if (event.getBlockPlaced().getType() == Material.TNT)
            this.tnt.put(BlockPosition.of(event.getBlockPlaced()), actor);
    }

    private List<BlockState> replaced(BlockPlaceEvent event) {

        if (event instanceof BlockMultiPlaceEvent multi)
            return multi.getReplacedBlockStates();

        return List.of(event.getBlockReplacedState());
    }
}
