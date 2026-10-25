package rip.alpha.blocklogging.config;

import lombok.Data;

@Data
public final class BlockLoggingConfig {

    private StorageConfig storage = new StorageConfig();

    private WriterConfig writer = new WriterConfig();

    private LoggingConfig logging = new LoggingConfig();

    private LookupConfig lookup = new LookupConfig();

    private RollbackConfig rollback = new RollbackConfig();
}
