package rip.alpha.blocklogging.rollback.guard;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import rip.alpha.blocklogging.common.permission.Permissions;
import rip.alpha.blocklogging.query.model.CuboidRadius;
import rip.alpha.blocklogging.query.model.GlobalRadius;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.query.model.Radius;
import rip.alpha.blocklogging.query.model.WorldRadius;
import rip.alpha.blocklogging.query.type.QueryFlag;

@UtilityClass
public class WideRollbacks {

    public boolean blocked(CommandSender sender, Query query, int maxRadius) {

        if (query.has(QueryFlag.COUNT) || query.has(QueryFlag.PREVIEW))
            return false;

        return wide(query.radius(), maxRadius) && !sender.hasPermission(Permissions.ROLLBACK_GLOBAL);
    }

    private boolean wide(Radius radius, int maxRadius) {
        int span = maxRadius * 2 + 1;

        return switch (radius) {
            case GlobalRadius _, WorldRadius _ -> true;
            case CuboidRadius cuboid -> cuboid.maxX() - cuboid.minX() + 1 > span || cuboid.maxZ() - cuboid.minZ() + 1 > span;
            default -> false;
        };
    }
}
