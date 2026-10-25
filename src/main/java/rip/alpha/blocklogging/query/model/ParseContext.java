package rip.alpha.blocklogging.query.model;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public record ParseContext(@Nullable Player player, int maxRadius) {

    @Nullable
    public Location location() {
        return this.player == null ? null : this.player.getLocation();
    }
}
