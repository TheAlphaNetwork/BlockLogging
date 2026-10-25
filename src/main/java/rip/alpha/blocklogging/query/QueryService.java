package rip.alpha.blocklogging.query;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.config.ConfigStore;
import rip.alpha.blocklogging.query.exception.QueryException;
import rip.alpha.blocklogging.query.model.LookupPage;
import rip.alpha.blocklogging.query.model.LookupView;
import rip.alpha.blocklogging.query.model.PageRequest;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.query.model.ResolvedQuery;
import rip.alpha.blocklogging.query.model.SavedLookup;
import rip.alpha.blocklogging.query.render.LookupRenderer;
import rip.alpha.blocklogging.query.sql.LookupSql;
import rip.alpha.blocklogging.query.sql.QueryResolver;
import rip.alpha.blocklogging.query.state.LastLookups;
import rip.alpha.blocklogging.storage.StorageService;
import rip.alpha.libraries.task.Tasks;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public final class QueryService {

    private final Plugin plugin;

    private final StorageService storage;

    private final ConfigStore configs;

    private final Logger logger;

    @Getter
    private final LastLookups lastLookups = new LastLookups();

    public void lookup(CommandSender sender, Query query, LookupView view) {
        SavedLookup lookup = new SavedLookup(query, view);

        this.lastLookups.remember(sender, lookup);
        Tasks.async(() -> run(sender, lookup, new PageRequest(1, pageSize(0))));
    }

    public void page(CommandSender sender, PageRequest request) {
        SavedLookup lookup = this.lastLookups.last(sender);

        if (lookup == null) {
            Chat.error(sender, "Run a lookup first.");
            return;
        }

        Tasks.async(() -> run(sender, lookup, new PageRequest(Math.max(1, request.page()), pageSize(request.size()))));
    }

    private void run(CommandSender sender, SavedLookup lookup, PageRequest request) {

        try (Connection connection = this.storage.connect()) {
            ResolvedQuery resolved = QueryResolver.resolve(connection, this.storage, lookup.query());

            LookupPage page = new LookupSql(connection, this.storage, resolved).page(request.page(), request.size());
            Tasks.sync(this.plugin, () -> LookupRenderer.render(sender, page, lookup.view()));

        } catch (QueryException exception) {
            Tasks.sync(this.plugin, () -> Chat.error(sender, exception.getMessage()));

        } catch (SQLException exception) {
            this.logger.log(Level.WARNING, "Lookup failed", exception);
            Tasks.sync(this.plugin, () -> Chat.error(sender, "The lookup failed, check the console."));
        }
    }

    private int pageSize(int requested) {
        int max = this.configs.getConfig().getLookup().getMaxPageSize();

        return requested <= 0 ? this.configs.getConfig().getLookup().getPageSize() : Math.clamp(requested, 1, max);
    }
}
