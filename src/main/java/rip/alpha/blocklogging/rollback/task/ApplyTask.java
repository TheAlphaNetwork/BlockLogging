package rip.alpha.blocklogging.rollback.task;

import org.bukkit.scheduler.BukkitRunnable;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.rollback.model.ApplyJob;
import rip.alpha.blocklogging.rollback.model.Change;
import rip.alpha.blocklogging.rollback.type.RollbackKind;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class ApplyTask extends BukkitRunnable {

    private static final long PROGRESS_MILLIS = TimeUnit.SECONDS.toMillis(5);

    private final Deque<Change> changes;

    private final ApplyJob job;

    private final ApplyCounter counter = new ApplyCounter();

    private final ChangeApplier applier;

    private final ProcessedChanges processed;

    private final int total;

    private final long started = System.currentTimeMillis();
    private long lastProgress = this.started;

    public ApplyTask(List<Change> changes, ApplyJob job) {
        this.changes = new ArrayDeque<>(changes);
        this.job = job;
        this.total = changes.size();
        this.applier = new ChangeApplier(job.sink());
        this.processed = new ProcessedChanges(job.sink(), job.kind() == RollbackKind.ROLLBACK);
    }

    @Override
    public void run() {
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(this.job.budgetMillis());

        while (!this.changes.isEmpty() && System.nanoTime() < deadline) {
            Change change = this.changes.pollFirst();

            this.counter.count(change, this.applier.apply(change));
            this.processed.add(change);
        }

        this.processed.flush();

        if (this.changes.isEmpty()) {
            cancel();

            this.job.done().accept(this.counter.result(System.currentTimeMillis() - this.started));
            return;
        }

        progress();
    }

    private void progress() {
        long now = System.currentTimeMillis();

        if (this.job.silent() || now - this.lastProgress < PROGRESS_MILLIS)
            return;

        int done = this.total - this.changes.size();

        this.lastProgress = now;

        String title = this.job.kind().getTitle();

        Chat.info(this.job.sender(), "%s %d%% done (%d/%d).".formatted(title, done * 100 / this.total, done, this.total));
    }
}
