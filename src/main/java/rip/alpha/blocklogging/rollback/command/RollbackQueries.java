package rip.alpha.blocklogging.rollback.command;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.config.RollbackConfig;
import rip.alpha.blocklogging.query.exception.QueryException;
import rip.alpha.blocklogging.query.model.AreaRadius;
import rip.alpha.blocklogging.query.model.GlobalRadius;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.query.model.QueryInput;
import rip.alpha.blocklogging.query.model.Radius;
import rip.alpha.blocklogging.query.parse.SenderQueries;
import rip.alpha.blocklogging.query.type.Action;

import java.util.EnumSet;
import java.util.Set;

@UtilityClass
class RollbackQueries {

    private final Set<Action> DEFAULTS = Set.of(Action.BLOCK, Action.CONTAINER, Action.KILL);

    Query parse(CommandSender sender, QueryInput input, RollbackConfig config) {
        Location location = SenderQueries.location(sender);

        boolean explicitRadius = SenderQueries.hasRadius(input);

        if (location == null && !explicitRadius && !SenderQueries.hasUser(input))
            throw new QueryException("Console needs a user (u:) or a radius (r:#global, r:#world).");

        Query query = SenderQueries.parse(sender, input, config.getMaxRadius(), fallback(location, config.getDefaultRadius()));

        if (config.isRequireTime() && query.since() == 0)
            throw new QueryException("Add a time so you don't change too much, e.g. t:1h.");

        return query.withActions(reversible(query.actions()));
    }

    private Radius fallback(@Nullable Location location, int radius) {

        if (location == null)
            return new GlobalRadius();

        return AreaRadius.builder()
                .world(location.getWorld().getName())
                .x(location.getBlockX())
                .z(location.getBlockZ())
                .size(radius)
                .build();
    }

    private Set<Action> reversible(Set<Action> actions) {

        if (actions.isEmpty())
            return DEFAULTS;

        Set<Action> kept = EnumSet.noneOf(Action.class);

        for (Action action : actions) {

            if (action.getTable().isReversible())
                kept.add(action);
        }

        if (kept.isEmpty())
            throw new QueryException("Only block, container and kill actions can be rolled back.");

        return Set.copyOf(kept);
    }
}
