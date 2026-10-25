package rip.alpha.blocklogging.config;

import lombok.Getter;
import rip.alpha.libraries.config.JsonConfigs;
import rip.alpha.libraries.config.exception.ConfigException;

import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ConfigStore {

    private static final String FILE = "blocklogging.json";

    private final JsonConfigs configs = new JsonConfigs();

    private final Path file;

    private final Logger logger;

    @Getter
    private volatile BlockLoggingConfig config = new BlockLoggingConfig();

    public ConfigStore(Path folder, Logger logger) {
        this.file = folder.resolve(FILE);
        this.logger = logger;
    }

    public void load() {

        try {
            this.config = this.configs.load(BlockLoggingConfig.class, this.file);
            this.configs.save(this.config, this.file);

        } catch (ConfigException exception) {
            this.logger.log(Level.SEVERE, "Could not load " + FILE + ", using the defaults", exception);
        }
    }

    public LoggingConfig logging() {
        return this.config.getLogging();
    }
}
