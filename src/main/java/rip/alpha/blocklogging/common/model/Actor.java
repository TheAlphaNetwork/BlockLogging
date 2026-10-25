package rip.alpha.blocklogging.common.model;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;

public record Actor(String name, @Nullable UUID uuid) {

    public static final String SYSTEM_PREFIX = "#";

    public static Actor player(Player player) {
        return new Actor(player.getName(), player.getUniqueId());
    }

    public static Actor system(String name) {
        return new Actor(SYSTEM_PREFIX + name.toLowerCase(Locale.ROOT), null);
    }

    public static Actor entity(Entity entity) {

        if (entity instanceof Player player)
            return player(player);

        return system(entity.getType().name());
    }

    public boolean isPlayer() {
        return this.uuid != null;
    }
}
