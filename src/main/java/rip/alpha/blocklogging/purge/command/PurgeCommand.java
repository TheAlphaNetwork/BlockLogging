package rip.alpha.blocklogging.purge.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.alpha.blocklogging.common.permission.Permissions;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.config.ConfigStore;
import rip.alpha.blocklogging.config.RollbackConfig;
import rip.alpha.blocklogging.purge.PurgeService;
import rip.alpha.blocklogging.query.exception.QueryException;
import rip.alpha.blocklogging.query.model.GlobalRadius;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.query.model.QueryInput;
import rip.alpha.blocklogging.query.model.WorldRadius;
import rip.alpha.blocklogging.query.parse.SenderQueries;
import rip.alpha.blocklogging.query.type.QueryFlag;
import rip.alpha.libraries.command.annotation.Command;
import rip.alpha.libraries.command.annotation.Param;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public final class PurgeCommand {

    private final PurgeService purges;

    private final ConfigStore configs;

    @Command(
            names = {"bl purge", "blocklogging purge", "co purge"},
            permission = Permissions.PURGE,
            description = "Delete logs older than the given time"
    )
    public void purge(
            CommandSender sender,
            @Param(name = "t:<time> [r:#world] [#optimize]", defaultValue = " ", wildcard = true) QueryInput input
    ) {

        try {
            Query query = SenderQueries.parse(sender, input, 0, new GlobalRadius());

            validate(sender, query);

            String world = query.radius() instanceof WorldRadius radius ? radius.world() : null;

            this.purges.purge(sender, query.since(), world, query.has(QueryFlag.OPTIMIZE));

        } catch (QueryException exception) {
            Chat.error(sender, exception.getMessage());
        }
    }

    private void validate(CommandSender sender, Query query) {
        RollbackConfig config = this.configs.getConfig().getRollback();

        if (query.since() == 0)
            throw new QueryException("Usage: /bl purge t:<time> [r:#world] [#optimize], e.g. t:30d.");

        if (!(query.radius() instanceof GlobalRadius) && !(query.radius() instanceof WorldRadius))
            throw new QueryException("A purge can only be limited to a world with r:#<world>.");

        if (!query.users().isEmpty() || !query.actions().isEmpty() || !query.include().isEmpty())
            throw new QueryException("A purge only takes a time and optionally a world.");

        long now = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        int minimum = sender instanceof Player ? config.getPurgeMinimumDaysInGame() : config.getPurgeMinimumDaysConsole();

        if (now - query.since() < TimeUnit.DAYS.toSeconds(minimum))
            throw new QueryException("You can only purge logs older than %d days from here.".formatted(minimum));
    }
}
