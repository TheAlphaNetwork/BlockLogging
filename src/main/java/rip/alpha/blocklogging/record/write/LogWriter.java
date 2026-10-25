package rip.alpha.blocklogging.record.write;

import lombok.Getter;
import lombok.Setter;
import rip.alpha.blocklogging.config.WriterConfig;
import rip.alpha.blocklogging.record.model.PendingWrite;
import rip.alpha.blocklogging.record.model.PurgeRequest;
import rip.alpha.blocklogging.record.model.PurgeResult;
import rip.alpha.blocklogging.storage.StorageService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class LogWriter implements Runnable {

    private static final int ATTEMPTS = 2;

    private final BlockingQueue<PendingWrite> queue;

    private final Supplier<WriterConfig> config;

    private final Logger logger;

    private final WriterSession session;

    @Getter
    private final WriterStats stats = new WriterStats();

    private volatile boolean running = true;

    @Getter
    @Setter
    private volatile boolean paused;

    public LogWriter(BlockingQueue<PendingWrite> queue, StorageService storage, Supplier<WriterConfig> config, Logger logger) {
        this.queue = queue;
        this.config = config;
        this.logger = logger;
        this.session = new WriterSession(storage, logger);
    }

    @Override
    public void run() {

        while (this.running || !this.queue.isEmpty()) {

            try {
                drain();
            } catch (InterruptedException _) {
                this.running = false;
            }
        }

        this.session.close();
    }

    public void stop() {
        this.running = false;
    }

    private void drain() throws InterruptedException {
        WriterConfig settings = this.config.get();

        if (this.paused && this.running) {
            TimeUnit.MILLISECONDS.sleep(settings.getFlushMillis());
            return;
        }

        PendingWrite first = this.queue.poll(settings.getFlushMillis(), TimeUnit.MILLISECONDS);

        if (first == null)
            return;

        List<PendingWrite> writes = new ArrayList<>(Math.min(settings.getBatchSize(), this.queue.size() + 1));

        writes.add(first);
        this.queue.drainTo(writes, settings.getBatchSize() - 1);
        write(new WriteBatch(writes), writes.size());
    }

    private void write(WriteBatch batch, int size) {
        boolean written = false;

        for (int attempt = 1; attempt <= ATTEMPTS && !written; attempt++)
            written = tryWrite(batch);

        if (!written)
            this.stats.failed(size);

        batch.getPurges().forEach(this::purge);
    }

    private boolean tryWrite(WriteBatch batch) {
        long started = System.currentTimeMillis();

        try {
            WriteContext context = this.session.context();

            context.getConnection().setAutoCommit(false);

            int rows = batch.insert(context);

            RolledBackWriter.apply(context, batch.getUpdates());
            context.getConnection().commit();
            context.getConnection().setAutoCommit(true);

            this.stats.batch(rows, System.currentTimeMillis() - started);
            return true;

        } catch (Exception exception) {
            this.logger.log(Level.WARNING, "Could not write a batch of logs", exception);
            this.session.reset();

            return false;
        }
    }

    private void purge(PurgeRequest request) {

        try {
            request.callback().accept(PurgeWriter.purge(this.session.context(), request));
        } catch (SQLException exception) {
            this.logger.log(Level.WARNING, "Could not purge logs", exception);
            this.session.reset();

            request.callback().accept(new PurgeResult(0, 0L, true));
        }
    }
}
