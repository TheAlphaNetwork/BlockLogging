package rip.alpha.blocklogging;

import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import rip.alpha.blocklogging.admin.command.AdminCommand;
import rip.alpha.blocklogging.admin.command.HelpCommand;
import rip.alpha.blocklogging.admin.command.TeleportCommand;
import rip.alpha.blocklogging.config.ConfigStore;
import rip.alpha.blocklogging.inspect.InspectService;
import rip.alpha.blocklogging.inspect.command.InspectCommand;
import rip.alpha.blocklogging.inspect.listener.InspectListener;
import rip.alpha.blocklogging.purge.PurgeService;
import rip.alpha.blocklogging.purge.command.PurgeCommand;
import rip.alpha.blocklogging.query.QueryService;
import rip.alpha.blocklogging.query.command.LookupCommand;
import rip.alpha.blocklogging.query.model.QueryInput;
import rip.alpha.blocklogging.query.parameter.QueryInputType;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.listener.RecordListeners;
import rip.alpha.blocklogging.rollback.RollbackService;
import rip.alpha.blocklogging.rollback.command.RollbackCommand;
import rip.alpha.blocklogging.rollback.listener.PreviewListener;
import rip.alpha.blocklogging.storage.StorageService;
import rip.alpha.libraries.Libraries;
import rip.alpha.libraries.command.CommandService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

@Getter
public final class BlockLogging extends JavaPlugin {

    private ConfigStore configStore;

    private StorageService storage;

    private RecordService records;

    private QueryService queries;

    private RollbackService rollbacks;

    @Override
    public void onEnable() {
        this.configStore = new ConfigStore(getDataFolder().toPath(), getLogger());
        this.configStore.load();
        this.storage = new StorageService(this.configStore.getConfig().getStorage(), getDataFolder().toPath());

        try {
            this.storage.install();
        } catch (SQLException | IOException exception) {
            getLogger().log(Level.SEVERE, "Could not open the " + this.storage.getDialect().name() + " database, disabling", exception);
            getServer().getPluginManager().disablePlugin(this);

            return;
        }

        this.records = new RecordService(this.storage, this.configStore, getLogger());
        this.queries = new QueryService(this, this.storage, this.configStore, getLogger());
        this.rollbacks = new RollbackService(this, this.storage, this.records, this.configStore, getLogger());
        this.records.start();

        InspectService inspect = new InspectService(this.queries);

        PurgeService purges = new PurgeService(this, this.records);

        PluginManager plugins = getServer().getPluginManager();

        RecordListeners.register(this, this.records);
        plugins.registerEvents(new InspectListener(inspect), this);
        plugins.registerEvents(new PreviewListener(this.rollbacks.previews()), this);

        CommandService commands = Libraries.get().getCommands();

        commands.parameters().register(QueryInput.class, new QueryInputType());
        commands.register(this,
                new HelpCommand(),
                new InspectCommand(inspect),
                new LookupCommand(this.queries, this.configStore),
                new RollbackCommand(this.rollbacks, this.configStore),
                new PurgeCommand(purges, this.configStore),
                new AdminCommand(this, this.configStore, this.storage, this.records, this.rollbacks),
                new TeleportCommand()
        );
    }

    @Override
    public void onDisable() {
        Libraries.get().getCommands().unregisterAll(this);

        if (this.records == null)
            return;

        try {
            this.records.stop();
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
        }
    }
}
