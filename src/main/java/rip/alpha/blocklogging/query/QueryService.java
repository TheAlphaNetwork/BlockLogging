package rip.alpha.blocklogging.query;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.config.ConfigStore;
import rip.alpha.blocklogging.query.exception.QueryException;
import rip.alpha.blocklogging.query.filter.StaffCommands;
import rip.alpha.blocklogging.query.menu.LookupMenu;
import rip.alpha.blocklogging.query.model.LookupPage;
import rip.alpha.blocklogging.query.model.LookupView;
import rip.alpha.blocklogging.query.model.NameColors;
import rip.alpha.blocklogging.query.model.PageRequest;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.query.model.ResolvedQuery;
import rip.alpha.blocklogging.query.model.SavedLookup;
import rip.alpha.blocklogging.query.render.LookupRenderer;
import rip.alpha.blocklogging.query.render.NameColoring;
import rip.alpha.blocklogging.query.state.LastLookups;
import rip.alpha.blocklogging.storage.exception.StoreException;
import rip.alpha.blocklogging.storage.sql.read.LookupSql;
import rip.alpha.blocklogging.storage.sql.read.QueryResolver;
import rip.alpha.blocklogging.storage.store.LogStore;
import rip.alpha.libraries.task.Tasks;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public final class QueryService {

    private final Plugin plugin;

    private final LogStore store;

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

    public void menu(Player player, Query query, LookupView view) {
        SavedLookup lookup = new SavedLookup(query, view);

        this.lastLookups.remember(player, lookup);
        menu(player, lookup, 1);
    }

    public void menu(Player player, int page) {
        SavedLookup lookup = this.lastLookups.last(player);

        if (lookup == null) {
            Chat.error(player, "Run a lookup first.");
            return;
        }

        menu(player, lookup, page);
    }

    public void menu(Player player, SavedLookup lookup, int page) {
        PageRequest request = new PageRequest(Math.max(1, page), LookupMenu.ROWS_PER_PAGE);

        Tasks.async(() -> fetch(player, lookup, request, (result, _) -> new LookupMenu(this, lookup, result).open(player)));
    }

    private void run(CommandSender sender, SavedLookup lookup, PageRequest request) {
        fetch(sender, lookup, request, (page, colors) -> LookupRenderer.render(sender, page, lookup.view(), colors));
    }

    private void fetch(CommandSender sender, SavedLookup lookup, PageRequest request, BiConsumer<LookupPage, NameColors> show) {

        try {
            LookupPage page = StaffCommands.redact(sender, this.store.lookup(lookup.query(), request));

            NameColors colors = NameColoring.of(page);

            Tasks.sync(this.plugin, () -> show.accept(page, colors));

        } catch (QueryException exception) {
            Tasks.sync(this.plugin, () -> Chat.error(sender, exception.getMessage()));

        } catch (StoreException exception) {
            this.logger.log(Level.WARNING, "Lookup failed", exception);
            Tasks.sync(this.plugin, () -> Chat.error(sender, "The lookup failed, check the console."));
        }
    }

    private int pageSize(int requested) {
        int max = this.configs.getConfig().getLookup().getMaxPageSize();

        return requested <= 0 ? this.configs.getConfig().getLookup().getPageSize() : Math.clamp(requested, 1, max);
    }
}
