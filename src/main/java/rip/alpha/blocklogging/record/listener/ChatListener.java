package rip.alpha.blocklogging.record.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.model.LogBase;
import rip.alpha.blocklogging.record.model.MessageRecord;
import rip.alpha.blocklogging.record.type.MessageChannel;

@RequiredArgsConstructor
public final class ChatListener implements Listener {

    private final RecordService records;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {

        if (this.records.logging().isChat())
            log(event.getPlayer(), MessageChannel.CHAT, event.getMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {

        if (this.records.logging().isCommands())
            log(event.getPlayer(), MessageChannel.COMMAND, event.getMessage());
    }

    private void log(Player player, MessageChannel channel, String text) {
        LogBase base = LogBase.now(Actor.player(player), BlockPosition.of(player.getLocation()));

        this.records.submit(new MessageRecord(base, channel, text));
    }
}
