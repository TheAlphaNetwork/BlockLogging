package rip.alpha.blocklogging.admin.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import rip.alpha.blocklogging.admin.menu.PanelButtons;
import rip.alpha.blocklogging.admin.menu.PanelMenu;
import rip.alpha.blocklogging.common.permission.Permissions;
import rip.alpha.blocklogging.query.QueryService;
import rip.alpha.libraries.command.annotation.Command;
import rip.alpha.libraries.command.annotation.Param;

@RequiredArgsConstructor
public final class MenuCommand {

    private final QueryService queries;

    private final PanelButtons buttons;

    @Command(
            names = {"bl menu", "bl panel", "blocklogging menu", "co menu"},
            permission = Permissions.LOOKUP,
            description = "Open the BlockLogging control panel",
            playerOnly = true
    )
    public void menu(Player player) {
        new PanelMenu(this.buttons).open(player);
    }

    @Command(
            names = {"bl gui", "blocklogging gui", "co gui"},
            permission = Permissions.LOOKUP,
            description = "Open your last lookup in a menu",
            playerOnly = true
    )
    public void gui(Player player, @Param(name = "page", defaultValue = "1") int page) {
        this.queries.menu(player, page);
    }
}
