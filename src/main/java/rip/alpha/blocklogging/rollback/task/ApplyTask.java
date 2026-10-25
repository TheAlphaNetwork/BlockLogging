package rip.alpha.blocklogging.rollback.task;

import org.bukkit.scheduler.BukkitRunnable;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.rollback.apply.BlockApplier;
import rip.alpha.blocklogging.rollback.apply.ContainerApplier;
import rip.alpha.blocklogging.rollback.apply.EntityApplier;
import rip.alpha.blocklogging.rollback.model.ApplyJob;
import rip.alpha.blocklogging.rollback.model.ApplyResult;
import rip.alpha.blocklogging.rollback.model.BlockTarget;
import rip.alpha.blocklogging.rollback.model.Change;
import rip.alpha.blocklogging.rollback.model.ContainerChange;
import rip.alpha.blocklogging.rollback.model.EntityRespawn;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class ApplyTask extends BukkitRunnable {

    private static final long PROGRESS_MILLIS = TimeUnit.SECONDS.toMillis(5);

    private final Deque<Change> changes;

    private final ApplyJob job;

    private final ApplyCounter counter = new ApplyCounter();

    private final int total;

    private final long started = System.currentTimeMillis();
    private long lastProgress = this.started;

    public ApplyTask(List<Change> changes, ApplyJob job) {
        this.changes = new ArrayDeque<>(changes);
        this.job = job;
        this.total = changes.size();
    }

    @Override
    public void run() {
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(this.job.budgetMillis());

        while (!this.changes.isEmpty() && System.nanoTime() < deadline)
            apply(this.changes.pollFirst());

        if (this.changes.isEmpty()) {
            cancel();

            this.job.done().accept(this.counter.result(System.currentTimeMillis() - this.started));
            return;
        }

        progress();
    }

    private void apply(Change change) {

        try {
            boolean applied = switch (change) {
                case BlockTarget target -> BlockApplier.apply(target);
                case ContainerChange container -> ContainerApplier.apply(container);
                case EntityRespawn respawn -> EntityApplier.apply(respawn);
            };

            this.counter.count(change, applied);

        } catch (RuntimeException _) {
            this.counter.count(change, false);
        }
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
