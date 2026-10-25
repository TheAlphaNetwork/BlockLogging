package rip.alpha.blocklogging.record.capture;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.Actor;

@UtilityClass
public class Causes {

    public final Actor FIRE = Actor.system("fire");
    public final Actor DECAY = Actor.system("decay");
    public final Actor PISTON = Actor.system("piston");
    public final Actor HOPPER = Actor.system("hopper");
    public final Actor TREE = Actor.system("tree");
    public final Actor WATER = Actor.system("water");
    public final Actor LAVA = Actor.system("lava");
    public final Actor EXPLOSION = Actor.system("explosion");

    public Actor explosion(@Nullable Entity source) {

        if (source == null)
            return EXPLOSION;

        return switch (source.getType()) {
            case PRIMED_TNT -> Actor.system("tnt");
            case MINECART_TNT -> Actor.system("tnt_minecart");
            case FIREBALL, SMALL_FIREBALL -> Actor.system("fireball");
            default -> Actor.entity(source);
        };
    }

    public Actor killer(LivingEntity entity) {

        if (entity.getKiller() != null)
            return Actor.player(entity.getKiller());

        EntityDamageEvent damage = entity.getLastDamageCause();

        if (damage instanceof EntityDamageByEntityEvent byEntity)
            return attacker(byEntity.getDamager());

        return damage == null ? Actor.system("unknown") : Actor.system(damage.getCause().name());
    }

    private Actor attacker(Entity damager) {

        if (damager instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter)
            return Actor.entity(shooter);

        return damager.getType() == EntityType.PRIMED_TNT ? Actor.system("tnt") : Actor.entity(damager);
    }
}
