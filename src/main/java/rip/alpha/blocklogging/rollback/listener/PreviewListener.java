package rip.alpha.blocklogging.rollback.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import rip.alpha.blocklogging.rollback.PreviewService;

@RequiredArgsConstructor
public final class PreviewListener implements Listener {

    private final PreviewService previews;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        this.previews.discard(event.getPlayer());
    }
}
