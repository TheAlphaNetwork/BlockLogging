package rip.alpha.blocklogging.rollback.model;

import lombok.Builder;
import org.bukkit.plugin.Plugin;
import rip.alpha.blocklogging.config.ConfigStore;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.rollback.state.LastOperations;
import rip.alpha.blocklogging.rollback.state.PreviewHandler;
import rip.alpha.blocklogging.storage.store.LogStore;

import java.util.logging.Logger;

@Builder
public record RunnerContext(
        Plugin plugin,
        LogStore store,
        RecordService records,
        ConfigStore configs,
        Logger logger,
        LastOperations operations,
        PreviewHandler previews
) {
}
