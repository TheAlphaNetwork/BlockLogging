package rip.alpha.blocklogging.admin.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.libraries.menu.Menu;
import rip.alpha.libraries.menu.button.Button;
import rip.alpha.libraries.menu.fill.FillTemplate;

import java.util.Map;

@RequiredArgsConstructor
public final class PanelMenu extends Menu {

    private final PanelButtons buttons;

    @Override
    public String title(Player player) {
        return Chat.PRIMARY + "BlockLogging";
    }

    @Override
    public int size() {
        return 27;
    }

    @Override
    public Map<Integer, Button> buttons(Player player) {
        return this.buttons.of(player);
    }

    @Override
    public FillTemplate fill() {
        return FillTemplate.FILL;
    }
}
