package rip.alpha.blocklogging.rollback.apply;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.rollback.model.EntityRemoval;
import rip.alpha.blocklogging.rollback.model.EntityRespawn;

import java.util.UUID;

@UtilityClass
public class EntityApplier {

    @Nullable
    public UUID spawn(EntityRespawn respawn) {
        Location location = respawn.position().center();

        if (location == null)
            return null;

        Entity entity = location.getWorld().spawnEntity(location, respawn.type());

        if (entity instanceof LivingEntity living && respawn.meta() != null)
            EntityTraitApplier.apply(living, respawn.meta());

        return entity.getUniqueId();
    }

    public boolean remove(EntityRemoval removal) {
        Block block = removal.position().block();

        if (removal.entity() == null || block == null)
            return false;

        for (Entity entity : block.getWorld().getEntities()) {

            if (entity.getUniqueId().equals(removal.entity())) {
                entity.remove();
                return true;
            }
        }

        return false;
    }
}
