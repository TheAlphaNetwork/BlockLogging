package rip.alpha.blocklogging.record.capture;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.state.PositionOwners;

@UtilityClass
class TntSources {

    private final Actor TNT = Actor.system("tnt");

    Actor of(TNTPrimed primed, PositionOwners owners) {

        if (primed.getSource() instanceof Player player)
            return Actor.player(player);

        Location origin = primed.getSourceLoc();

        Actor owner = origin == null ? null : owners.get(BlockPosition.of(origin));

        return owner == null ? TNT : owner;
    }
}
