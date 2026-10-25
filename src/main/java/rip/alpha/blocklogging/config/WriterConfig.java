package rip.alpha.blocklogging.config;

import lombok.Data;

@Data
public final class WriterConfig {

    private int flushMillis = 500;
    private int batchSize = 2000;
    private int maxQueued = 500_000;
}
