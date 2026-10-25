package rip.alpha.blocklogging.rollback.apply;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import rip.alpha.blocklogging.rollback.model.EntityRespawn;

@UtilityClass
public class EntityApplier {

    public boolean apply(EntityRespawn respawn) {
        Location location = respawn.position().center();

        if (location == null)
            return false;

        location.getWorld().spawnEntity(location, respawn.type());
        return true;
    }
}
