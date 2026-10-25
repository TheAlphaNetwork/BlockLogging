package rip.alpha.blocklogging.record.write;

import java.util.concurrent.atomic.AtomicLong;

public final class WriterStats {

    private final AtomicLong written = new AtomicLong();
    private final AtomicLong failed = new AtomicLong();
    private final AtomicLong dropped = new AtomicLong();
    private final AtomicLong lastBatchMillis = new AtomicLong();

    void batch(int rows, long millis) {
        this.written.addAndGet(rows);
        this.lastBatchMillis.set(millis);
    }

    void failed(int rows) {
        this.failed.addAndGet(rows);
    }

    public void dropped() {
        this.dropped.incrementAndGet();
    }

    public long written() {
        return this.written.get();
    }

    public long failedRows() {
        return this.failed.get();
    }

    public long droppedRows() {
        return this.dropped.get();
    }

    public long lastBatchMillis() {
        return this.lastBatchMillis.get();
    }
}
