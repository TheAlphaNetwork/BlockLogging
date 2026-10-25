package rip.alpha.blocklogging.inspect.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import rip.alpha.blocklogging.common.permission.Permissions;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.inspect.InspectService;
import rip.alpha.libraries.command.annotation.Command;

@RequiredArgsConstructor
public final class InspectCommand {

    private final InspectService inspect;

    @Command(
            names = {"bl inspect", "bl i"},
            permission = Permissions.INSPECT,
            description = "Toggle the block inspector",
            playerOnly = true
    )
    public void inspect(Player player) {

        if (!this.inspect.toggle(player)) {
            Chat.info(player, "Inspector " + Chat.TEXT + "disabled" + Chat.MUTED + ".");
            return;
        }

        Chat.info(player, "Inspector " + Chat.ACCENT + "enabled" + Chat.MUTED + ".");
        Chat.info(player, "Left click a block for its history, right click a container or the space next to a block.");
    }
}
