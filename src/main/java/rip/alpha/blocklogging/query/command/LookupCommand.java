package rip.alpha.blocklogging.query.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.alpha.blocklogging.common.permission.Permissions;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.config.ConfigStore;
import rip.alpha.blocklogging.query.QueryService;
import rip.alpha.blocklogging.query.exception.QueryException;
import rip.alpha.blocklogging.query.model.AreaRadius;
import rip.alpha.blocklogging.query.model.GlobalRadius;
import rip.alpha.blocklogging.query.model.LookupView;
import rip.alpha.blocklogging.query.model.PageRequest;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.query.model.QueryInput;
import rip.alpha.blocklogging.query.parse.PageRequests;
import rip.alpha.blocklogging.query.parse.SenderQueries;
import rip.alpha.blocklogging.query.type.Action;
import rip.alpha.libraries.command.annotation.Command;
import rip.alpha.libraries.command.annotation.Param;

import java.util.Collections;

@RequiredArgsConstructor
public final class LookupCommand {

    private static final String USAGE = "Usage: /bl lookup u:<user> t:<time> r:<radius> a:<action> i:<include> e:<exclude>";

    private final QueryService queries;

    private final ConfigStore configs;

    @Command(
            names = {"bl lookup", "bl l", "blocklogging lookup", "blocklogging l", "co lookup", "co l"},
            permission = Permissions.LOOKUP,
            description = "Search the logs, or open another page of the last search"
    )
    public void lookup(CommandSender sender, @Param(name = "filters", defaultValue = " ", wildcard = true) QueryInput input) {

        if (input.isEmpty()) {
            Chat.error(sender, USAGE);
            return;
        }

        PageRequest page = PageRequests.parse(input);

        if (page != null) {
            this.queries.page(sender, page);
            return;
        }

        try {
            Query query = SenderQueries.parse(sender, input, 0, new GlobalRadius());

            run(sender, query.actions().isEmpty() ? query.withActions(Action.LOOKUP_DEFAULTS) : query);

        } catch (QueryException exception) {
            Chat.error(sender, exception.getMessage());
        }
    }

    @Command(
            names = {"bl near", "blocklogging near", "co near"},
            permission = Permissions.LOOKUP,
            description = "Show recent changes around you",
            playerOnly = true
    )
    public void near(Player player, @Param(name = "radius", defaultValue = "0") int radius) {
        Location location = player.getLocation();

        int size = radius > 0 ? Math.min(radius, this.configs.getConfig().getRollback().getMaxRadius()) : nearRadius();

        AreaRadius area = AreaRadius.builder()
                .world(location.getWorld().getName())
                .x(location.getBlockX())
                .z(location.getBlockZ())
                .size(size)
                .build();

        run(player, Query.at(area, Action.LOOKUP_DEFAULTS));
    }

    private void run(CommandSender sender, Query query) {

        if (!Collections.disjoint(query.actions(), Action.PRIVATE) && !sender.hasPermission(Permissions.LOOKUP_CHAT)) {
            Chat.error(sender, "You are not allowed to look up chat or commands.");
            return;
        }

        this.queries.lookup(sender, query, new LookupView("Lookup Results", true));
    }

    private int nearRadius() {
        return this.configs.getConfig().getLookup().getNearRadius();
    }
}
