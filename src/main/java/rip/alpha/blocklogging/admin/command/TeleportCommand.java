package rip.alpha.blocklogging.admin.command;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import rip.alpha.blocklogging.common.permission.Permissions;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.libraries.command.annotation.Command;
import rip.alpha.libraries.command.annotation.Param;

public final class TeleportCommand {

    @Command(
            names = {"bl teleport", "bl tp", "blocklogging teleport", "blocklogging tp", "co teleport", "co tp"},
            permission = Permissions.TELEPORT,
            playerOnly = true,
            hidden = true
    )
    public void teleport(
            Player player,
            @Param(name = "world") World world,
            @Param(name = "x") int x,
            @Param(name = "y") int y,
            @Param(name = "z") int z
    ) {
        Location target = new Location(world, x + 0.5, y, z + 0.5, player.getLocation().getYaw(), player.getLocation().getPitch());

        player.teleport(target);
        Chat.info(player, "Teleported to %d/%d/%d/%s.".formatted(x, y, z, world.getName()));
    }
}
