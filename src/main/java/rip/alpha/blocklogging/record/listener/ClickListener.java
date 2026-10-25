package rip.alpha.blocklogging.record.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.capture.BlockCapture;
import rip.alpha.blocklogging.record.capture.Interactables;

@RequiredArgsConstructor
public final class ClickListener implements Listener {

    private final RecordService records;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || block == null || event.useInteractedBlock() == Event.Result.DENY)
            return;

        if (this.records.logging().isClicks() && Interactables.is(block.getType()))
            this.records.submit(BlockCapture.clicked(Actor.player(event.getPlayer()), block));
    }
}
