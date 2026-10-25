package rip.alpha.blocklogging.query.parse;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.query.model.ParseContext;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.query.model.QueryInput;
import rip.alpha.blocklogging.query.model.Radius;

import java.util.Locale;

@UtilityClass
public class SenderQueries {

    public Query parse(CommandSender sender, QueryInput input, int maxRadius, Radius fallback) {
        return QueryParser.parse(input, new ParseContext(location(sender), maxRadius), fallback);
    }

    @Nullable
    public Location location(CommandSender sender) {
        return sender instanceof Player player ? player.getLocation() : null;
    }

    public boolean hasRadius(QueryInput input) {
        return has(input, "r:", "radius:");
    }

    public boolean hasUser(QueryInput input) {
        return has(input, "u:", "user:", "users:", "p:", "player:");
    }

    private boolean has(QueryInput input, String... prefixes) {

        for (String token : input.tokens()) {
            String lower = token.toLowerCase(Locale.ROOT);

            for (String prefix : prefixes) {

                if (lower.startsWith(prefix))
                    return true;
            }
        }

        return false;
    }
}
