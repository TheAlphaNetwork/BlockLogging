package rip.alpha.blocklogging.record.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.model.LogBase;
import rip.alpha.blocklogging.record.model.SignRecord;

@RequiredArgsConstructor
public final class SignListener implements Listener {

    public static final String LINE_BREAK = "\n";

    private final RecordService records;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSign(SignChangeEvent event) {

        if (!this.records.logging().isSignText())
            return;

        LogBase base = LogBase.now(Actor.player(event.getPlayer()), BlockPosition.of(event.getBlock()));

        this.records.submit(new SignRecord(base, String.join(LINE_BREAK, event.getLines())));
    }
}
