package rip.alpha.blocklogging.record.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.model.LogBase;
import rip.alpha.blocklogging.record.model.SessionRecord;
import rip.alpha.blocklogging.record.type.SessionAction;

@RequiredArgsConstructor
public final class SessionListener implements Listener {

    private final RecordService records;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        log(event.getPlayer(), SessionAction.JOIN);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        log(event.getPlayer(), SessionAction.QUIT);
    }

    private void log(Player player, SessionAction action) {

        if (!this.records.logging().isSessions())
            return;

        LogBase base = LogBase.now(Actor.player(player), BlockPosition.of(player.getLocation()));

        this.records.submit(new SessionRecord(base, action));
    }
}
