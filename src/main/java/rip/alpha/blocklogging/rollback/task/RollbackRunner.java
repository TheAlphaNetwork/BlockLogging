package rip.alpha.blocklogging.rollback.task;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.query.exception.QueryException;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.query.type.QueryFlag;
import rip.alpha.blocklogging.rollback.journal.OperationJournal;
import rip.alpha.blocklogging.rollback.message.RollbackMessages;
import rip.alpha.blocklogging.rollback.model.ApplyJob;
import rip.alpha.blocklogging.rollback.model.Operation;
import rip.alpha.blocklogging.rollback.model.RollbackPlan;
import rip.alpha.blocklogging.rollback.model.RunKey;
import rip.alpha.blocklogging.rollback.model.RunnerContext;
import rip.alpha.blocklogging.rollback.plan.PlanLoader;
import rip.alpha.blocklogging.storage.exception.StoreException;
import rip.alpha.libraries.task.Tasks;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public final class RollbackRunner {

    private final AtomicInteger active = new AtomicInteger();

    private final Set<RunKey> running = ConcurrentHashMap.newKeySet();

    private final RunnerContext context;

    private final OperationJournal journal;

    public RollbackRunner(RunnerContext context) {
        this.context = context;
        this.journal = new OperationJournal(context.records());
    }

    public void run(CommandSender sender, String owner, Operation operation) {

        if (!this.running.add(RunKey.of(owner, operation))) {
            Chat.error(sender, "That %s is already running.".formatted(operation.kind().getTitle().toLowerCase(Locale.ROOT)));
            return;
        }

        RollbackMessages.started(sender, operation);
        Tasks.async(() -> plan(sender, owner, operation));
    }

    public int active() {
        return this.active.get();
    }

    private void plan(CommandSender sender, String owner, Operation operation) {

        try {
            RollbackPlan plan = PlanLoader.load(this.context.store(), operation);

            Tasks.sync(this.context.plugin(), () -> execute(sender, owner, operation, plan));

        } catch (QueryException exception) {
            release(owner, operation);
            Tasks.sync(this.context.plugin(), () -> Chat.error(sender, exception.getMessage()));

        } catch (StoreException exception) {
            String title = operation.kind().getTitle().toLowerCase(Locale.ROOT);

            release(owner, operation);
            this.context.logger().log(Level.WARNING, "Could not plan a " + title, exception);
            Tasks.sync(this.context.plugin(), () -> Chat.error(sender, "The %s failed, check the console.".formatted(title)));
        }
    }

    private void execute(CommandSender sender, String owner, Operation operation, RollbackPlan plan) {
        Query query = operation.query();

        boolean applies = !plan.isEmpty() && !query.has(QueryFlag.COUNT) && !(query.has(QueryFlag.PREVIEW) && sender instanceof Player);

        if (!applies)
            release(owner, operation);

        if (plan.isEmpty()) {
            Chat.info(sender, "No matching changes were found.");
            return;
        }

        if (query.has(QueryFlag.COUNT)) {
            RollbackMessages.count(sender, operation, plan);
            return;
        }

        if (query.has(QueryFlag.PREVIEW) && sender instanceof Player player) {
            this.context.previews().show(player, operation, plan);
            return;
        }

        start(sender, owner, operation, plan);
    }

    private void start(CommandSender sender, String owner, Operation operation, RollbackPlan plan) {
        ApplyJob job = ApplyJob.builder()
                .sender(sender)
                .kind(operation.kind())
                .silent(operation.query().has(QueryFlag.SILENT))
                .budgetMillis(Math.max(1, this.context.configs().getConfig().getRollback().getMillisPerTick()))
                .sink(this.context.records()::submit)
                .done(result -> {

                    this.active.decrementAndGet();
                    release(owner, operation);
                    this.context.operations().remember(owner, operation);

                    this.journal.finished(owner, operation);
                    RollbackMessages.finished(sender, operation, result);
                })
                .build();

        this.journal.started(owner, operation);

        this.active.incrementAndGet();
        new ApplyTask(plan.changes(), job).runTaskTimer(this.context.plugin(), 1L, 1L);
    }

    private void release(String owner, Operation operation) {
        this.running.remove(RunKey.of(owner, operation));
    }
}
