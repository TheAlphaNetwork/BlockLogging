package rip.alpha.blocklogging.record;

import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.config.ConfigStore;
import rip.alpha.blocklogging.config.LoggingConfig;
import rip.alpha.blocklogging.record.model.LogRecord;
import rip.alpha.blocklogging.record.model.PendingWrite;
import rip.alpha.blocklogging.record.write.LogWriter;
import rip.alpha.blocklogging.record.write.WriterStats;
import rip.alpha.blocklogging.storage.store.LogStore;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class RecordService {

    private static final long SHUTDOWN_SECONDS = 30L;

    private final ConfigStore configs;

    private final BlockingQueue<PendingWrite> queue;

    private final LogWriter writer;

    @Nullable
    private Thread thread;

    public RecordService(LogStore store, ConfigStore configs, Logger logger) {
        this.configs = configs;
        this.queue = new LinkedBlockingQueue<>(configs.getConfig().getWriter().getMaxQueued());
        this.writer = new LogWriter(this.queue, store, () -> configs.getConfig().getWriter(), logger);
    }

    public void start() {
        this.thread = Thread.ofPlatform().name("blocklogging-writer").start(this.writer);
    }

    public void stop() throws InterruptedException {
        this.writer.stop();

        if (this.thread != null)
            this.thread.join(TimeUnit.SECONDS.toMillis(SHUTDOWN_SECONDS));
    }

    public void submit(PendingWrite write) {

        if (write instanceof LogRecord record && logging().isDisabled(record.base().position().world()))
            return;

        if (!this.queue.offer(write))
            this.writer.getStats().dropped();
    }

    public LoggingConfig logging() {
        return this.configs.logging();
    }

    public int queued() {
        return this.queue.size();
    }

    public WriterStats stats() {
        return this.writer.getStats();
    }

    public boolean isPaused() {
        return this.writer.isPaused();
    }

    public boolean togglePaused() {
        this.writer.setPaused(!this.writer.isPaused());

        return this.writer.isPaused();
    }
}
