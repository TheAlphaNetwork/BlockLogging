package rip.alpha.blocklogging.rollback.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.rollback.menu.button.TargetChoiceButton;
import rip.alpha.blocklogging.rollback.state.RollbackDraft;
import rip.alpha.libraries.menu.Menu;
import rip.alpha.libraries.menu.button.BackButton;
import rip.alpha.libraries.menu.button.Button;
import rip.alpha.libraries.menu.page.PagedMenu;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public final class TargetMenu extends PagedMenu {

    private final Menu parent;

    private final RollbackDraft draft;

    @Override
    public String rawTitle(Player player) {
        return Chat.PRIMARY + "Pick a player";
    }

    @Override
    public Map<Integer, Button> allButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new TargetChoiceButton(this.parent, this.draft, null));

        for (Player online : Bukkit.getOnlinePlayers())
            buttons.put(buttons.size(), new TargetChoiceButton(this.parent, this.draft, online.getName()));

        return buttons;
    }

    @Override
    public Map<Integer, Button> globalButtons(Player player) {
        return Map.of(4, new BackButton(this.parent));
    }
}
