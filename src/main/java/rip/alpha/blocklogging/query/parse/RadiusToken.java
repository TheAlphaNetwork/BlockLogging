package rip.alpha.blocklogging.query.parse;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import rip.alpha.blocklogging.query.exception.QueryException;
import rip.alpha.blocklogging.query.model.AreaRadius;
import rip.alpha.blocklogging.query.model.GlobalRadius;
import rip.alpha.blocklogging.query.model.ParseContext;
import rip.alpha.blocklogging.query.model.Radius;
import rip.alpha.blocklogging.query.model.WorldRadius;

import java.util.Set;

final class RadiusToken implements QueryToken {

    private static final String GLOBAL = "#global";

    @Override
    public Set<String> prefixes() {
        return Set.of("r", "radius");
    }

    @Override
    public void apply(QueryDraft draft, String value, ParseContext context) {
        draft.setRadius(parse(value, context));
    }

    private Radius parse(String value, ParseContext context) {

        if (value.equalsIgnoreCase(GLOBAL))
            return new GlobalRadius();

        if (value.startsWith("#")) {
            String world = value.substring(1);

            if (Bukkit.getWorld(world) == null)
                throw new QueryException("Unknown world \"%s\".".formatted(world));

            return new WorldRadius(world);
        }

        Location location = context.location();

        if (location == null)
            throw new QueryException("Console has to use r:#global or r:#<world>.");

        return new AreaRadius(location.getWorld().getName(), location.getBlockX(), location.getBlockZ(), size(value, context));
    }

    private int size(String value, ParseContext context) {
        int size;

        try {
            size = Integer.parseInt(value);

        } catch (NumberFormatException _) {
            throw new QueryException("Invalid radius \"%s\".".formatted(value));
        }

        if (size < 0 || context.maxRadius() > 0 && size > context.maxRadius())
            throw new QueryException("The radius has to be between 0 and %d, or r:#global.".formatted(context.maxRadius()));

        return size;
    }
}
