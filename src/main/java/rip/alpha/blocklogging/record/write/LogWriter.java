package rip.alpha.blocklogging.record.write;

import lombok.Getter;
import lombok.Setter;
import rip.alpha.blocklogging.config.WriterConfig;
import rip.alpha.blocklogging.record.model.PendingWrite;
import rip.alpha.blocklogging.record.model.PurgeRequest;
import rip.alpha.blocklogging.record.model.PurgeResult;
import rip.alpha.blocklogging.storage.exception.StoreException;
import rip.alpha.blocklogging.storage.store.LogStore;

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

    private final LogStore store;

    private final Supplier<WriterConfig> config;

    private final Logger logger;

    @Getter
    private final WriterStats stats = new WriterStats();

    private volatile boolean running = true;

    @Getter
    @Setter
    private volatile boolean paused;

    public LogWriter(BlockingQueue<PendingWrite> queue, LogStore store, Supplier<WriterConfig> config, Logger logger) {
        this.queue = queue;
        this.store = store;
        this.config = config;
        this.logger = logger;
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
        write(writes);
    }

    private void write(List<PendingWrite> writes) {
        List<PurgeRequest> purges = new ArrayList<>();
        List<PendingWrite> logs = new ArrayList<>(writes.size());

        for (PendingWrite write : writes) {

            if (write instanceof PurgeRequest purge)
                purges.add(purge);
            else
                logs.add(write);
        }

        if (!logs.isEmpty() && !tryWrite(logs))
            this.stats.failed(logs.size());

        purges.forEach(this::purge);
    }

    private boolean tryWrite(List<PendingWrite> writes) {
        StoreException last = null;

        for (int attempt = 1; attempt <= ATTEMPTS; attempt++) {
            long started = System.currentTimeMillis();

            try {
                this.stats.batch(this.store.write(writes), System.currentTimeMillis() - started);
                return true;

            } catch (StoreException exception) {
                last = exception;
            }
        }

        this.logger.log(Level.WARNING, "Could not write a batch of logs", last);
        return false;
    }

    private void purge(PurgeRequest request) {

        try {
            request.callback().accept(this.store.purge(request));
        } catch (StoreException exception) {
            this.logger.log(Level.WARNING, "Could not purge logs", exception);
            request.callback().accept(new PurgeResult(0, 0L, true));
        }
    }
}
