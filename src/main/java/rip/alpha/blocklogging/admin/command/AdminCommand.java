package rip.alpha.blocklogging.admin.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import rip.alpha.blocklogging.common.permission.Permissions;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.config.ConfigStore;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.write.WriterStats;
import rip.alpha.blocklogging.rollback.RollbackService;
import rip.alpha.blocklogging.storage.StorageService;
import rip.alpha.libraries.command.annotation.Command;

@RequiredArgsConstructor
public final class AdminCommand {

    private final Plugin plugin;

    private final ConfigStore configs;

    private final StorageService storage;

    private final RecordService records;

    private final RollbackService rollbacks;

    @Command(
            names = {"bl status"},
            permission = Permissions.STATUS,
            description = "Show the logger and database status"
    )
    public void status(CommandSender sender) {
        WriterStats stats = this.records.stats();

        sender.sendMessage(Chat.header("Status"));
        line(sender, "Version", this.plugin.getDescription().getVersion());
        line(sender, "Database", this.storage.name());
        line(sender, "Queued", String.valueOf(this.records.queued()));
        line(sender, "Written", "%d (last batch %dms)".formatted(stats.written(), stats.lastBatchMillis()));
        line(sender, "Failed / dropped", "%d / %d".formatted(stats.failedRows(), stats.droppedRows()));
        line(sender, "Consumer", this.records.isPaused() ? "paused" : "running");
        line(sender, "Running rollbacks", String.valueOf(this.rollbacks.active()));
    }

    @Command(
            names = {"bl reload"},
            permission = Permissions.RELOAD,
            description = "Reload blocklogging.json"
    )
    public void reload(CommandSender sender) {
        this.configs.load();
        Chat.info(sender, "Reloaded the config. Database settings apply after a restart.");
    }

    @Command(
            names = {"bl consumer"},
            permission = Permissions.CONSUMER,
            description = "Pause or resume writing logs to the database"
    )
    public void consumer(CommandSender sender) {
        boolean paused = this.records.togglePaused();

        String state = paused ? "paused" : "running";

        Chat.info(sender, "The consumer is now %s%s%s, logs stay queued while paused.".formatted(Chat.TEXT, state, Chat.MUTED));
    }

    private void line(CommandSender sender, String key, String value) {
        sender.sendMessage(Chat.MUTED + key + ": " + Chat.TEXT + value);
    }
}
