package rip.alpha.blocklogging.rollback;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.config.ConfigStore;
import rip.alpha.blocklogging.query.exception.QueryException;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.query.model.ResolvedQuery;
import rip.alpha.blocklogging.query.sql.QueryResolver;
import rip.alpha.blocklogging.query.type.QueryFlag;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.rollback.message.RollbackMessages;
import rip.alpha.blocklogging.rollback.model.ApplyJob;
import rip.alpha.blocklogging.rollback.model.Operation;
import rip.alpha.blocklogging.rollback.model.RollbackPlan;
import rip.alpha.blocklogging.rollback.plan.PlanBuilder;
import rip.alpha.blocklogging.rollback.state.LastOperations;
import rip.alpha.blocklogging.rollback.task.ApplyTask;
import rip.alpha.blocklogging.rollback.type.RollbackKind;
import rip.alpha.blocklogging.storage.StorageService;
import rip.alpha.libraries.task.Tasks;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public final class RollbackService {

    private final Plugin plugin;

    private final StorageService storage;

    private final RecordService records;

    private final ConfigStore configs;

    private final Logger logger;

    private final LastOperations operations = new LastOperations();

    private final PreviewService previews = new PreviewService(this);

    private final AtomicInteger active = new AtomicInteger();

    public void run(CommandSender sender, Operation operation) {

        if (!operation.query().has(QueryFlag.SILENT))
            Chat.info(sender, "%s started, searching the logs...".formatted(operation.kind().getTitle()));

        Tasks.async(() -> plan(sender, operation));
    }

    public void undo(CommandSender sender) {
        Operation last = this.operations.last(sender);

        if (last == null) {
            Chat.error(sender, "You have nothing to undo.");
            return;
        }

        run(sender, new Operation(last.query(), last.kind().inverse()));
    }

    public PreviewService previews() {
        return this.previews;
    }

    public int active() {
        return this.active.get();
    }

    private void plan(CommandSender sender, Operation operation) {

        try (Connection connection = this.storage.connect()) {
            ResolvedQuery resolved = QueryResolver.resolve(connection, this.storage, operation.query());

            RollbackPlan plan = PlanBuilder.build(connection, this.storage, resolved, operation.kind());

            Tasks.sync(this.plugin, () -> execute(sender, operation, plan));

        } catch (QueryException exception) {
            Tasks.sync(this.plugin, () -> Chat.error(sender, exception.getMessage()));
        } catch (SQLException exception) {
            this.logger.log(Level.WARNING, "Could not plan a " + operation.kind().getTitle(), exception);
            Tasks.sync(this.plugin, () -> Chat.error(sender, "The %s failed, check the console.".formatted(operation.kind().getVerb())));
        }
    }

    private void execute(CommandSender sender, Operation operation, RollbackPlan plan) {
        Query query = operation.query();

        if (plan.isEmpty()) {
            Chat.info(sender, "No matching changes were found.");
            return;
        }

        if (query.has(QueryFlag.COUNT)) {
            RollbackMessages.count(sender, operation, plan);
            return;
        }

        if (query.has(QueryFlag.PREVIEW) && sender instanceof Player player) {
            this.previews.show(player, operation, plan);
            return;
        }

        start(sender, operation, plan);
    }

    private void start(CommandSender sender, Operation operation, RollbackPlan plan) {
        long budget = Math.max(1, this.configs.getConfig().getRollback().getMillisPerTick());

        boolean silent = operation.query().has(QueryFlag.SILENT);
        ApplyJob job = new ApplyJob(sender, operation.kind(), silent, budget, result -> {
            this.active.decrementAndGet();
            plan.flags().forEach(this.records::submit);
            this.operations.remember(sender, operation);
            RollbackMessages.finished(sender, operation, result);
        });

        this.active.incrementAndGet();
        new ApplyTask(plan.changes(), job).runTaskTimer(this.plugin, 1L, 1L);
    }
}
