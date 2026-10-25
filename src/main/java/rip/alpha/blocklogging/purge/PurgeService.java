package rip.alpha.blocklogging.purge;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.model.PurgeRequest;
import rip.alpha.blocklogging.record.model.PurgeResult;
import rip.alpha.libraries.task.Tasks;

@RequiredArgsConstructor
public final class PurgeService {

    private final Plugin plugin;

    private final RecordService records;

    public void purge(CommandSender sender, long before, @Nullable String world, boolean optimize) {
        Chat.info(sender, "Purge queued, it runs after the pending logs are written.");
        PurgeRequest request = PurgeRequest.builder()
                .before(before)
                .world(world)
                .optimize(optimize)
                .callback(result -> Tasks.sync(this.plugin, () -> report(sender, result)))
                .build();

        this.records.submit(request);
    }

    private void report(CommandSender sender, PurgeResult result) {

        if (result.failed()) {
            Chat.error(sender, "The purge failed, check the console.");
            return;
        }

        String rows = Chat.TEXT + result.rows() + Chat.MUTED;
        String time = Chat.TEXT + result.millis() + "ms" + Chat.MUTED;

        Chat.info(sender, "Purged %s rows in %s.".formatted(rows, time));
    }
}
