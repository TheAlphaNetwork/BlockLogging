package rip.alpha.blocklogging.rollback;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.config.ConfigStore;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.rollback.model.Operation;
import rip.alpha.blocklogging.rollback.model.OperationRecord;
import rip.alpha.blocklogging.rollback.model.RunnerContext;
import rip.alpha.blocklogging.rollback.state.LastOperations;
import rip.alpha.blocklogging.rollback.task.RollbackRunner;
import rip.alpha.blocklogging.rollback.type.OperationState;
import rip.alpha.blocklogging.storage.exception.StoreException;
import rip.alpha.blocklogging.storage.store.LogStore;
import rip.alpha.libraries.task.Tasks;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class RollbackService {

    private final Plugin plugin;

    private final LogStore store;

    private final Logger logger;

    private final LastOperations operations = new LastOperations();

    private final PreviewService previews = new PreviewService(this);

    private final RollbackRunner runner;

    public RollbackService(Plugin plugin, LogStore store, RecordService records, ConfigStore configs, Logger logger) {
        this.plugin = plugin;
        this.store = store;
        this.logger = logger;
        this.runner = new RollbackRunner(RunnerContext.builder()
                .plugin(plugin)
                .store(store)
                .records(records)
                .configs(configs)
                .logger(logger)
                .operations(this.operations)
                .previews(this.previews::show)
                .build());
    }

    public void run(CommandSender sender, Operation operation) {
        this.runner.run(sender, sender.getName(), operation);
    }

    public void undo(CommandSender sender) {
        Operation last = this.operations.last(sender.getName());

        if (last == null) {
            Chat.error(sender, "You have nothing to undo.");
            return;
        }

        run(sender, last.inverse());
    }

    public void restore() {
        Tasks.async(() -> {

            try {
                List<OperationRecord> records = this.store.operations();

                Tasks.sync(this.plugin, () -> records.forEach(this::restore));

            } catch (StoreException exception) {
                this.logger.log(Level.WARNING, "Could not load previous rollbacks", exception);
            }
        });
    }

    public PreviewService previews() {
        return this.previews;
    }

    public int active() {
        return this.runner.active();
    }

    private void restore(OperationRecord record) {

        if (record.state() == OperationState.DONE) {
            this.operations.remember(record.sender(), record.operation());
            return;
        }

        this.logger.info("Resuming an interrupted %s by %s".formatted(record.operation().kind().getVerb(), record.sender()));
        this.runner.run(Bukkit.getConsoleSender(), record.sender(), record.operation());
    }
}
