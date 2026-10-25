package rip.alpha.blocklogging.rollback.menu;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.rollback.RollbackService;
import rip.alpha.blocklogging.rollback.state.RollbackDraft;
import rip.alpha.libraries.menu.Menu;
import rip.alpha.libraries.menu.button.Button;
import rip.alpha.libraries.menu.fill.FillTemplate;

import java.util.Map;

public final class RollbackMenu extends Menu {

    private final RollbackDraft draft = new RollbackDraft();

    private final RollbackButtons buttons;

    public RollbackMenu(RollbackService rollbacks, @Nullable Menu parent) {
        this.buttons = new RollbackButtons(this, parent, this.draft, rollbacks);
    }

    @Override
    public String title(Player player) {
        return Chat.PRIMARY + "Rollback Builder";
    }

    @Override
    public int size() {
        return 54;
    }

    @Override
    public Map<Integer, Button> buttons(Player player) {
        return this.buttons.of(player);
    }

    @Override
    public FillTemplate fill() {
        return FillTemplate.FILL;
    }

    @Override
    public boolean autoUpdate() {
        return false;
    }

    @Override
    public boolean clickUpdate() {
        return true;
    }
}
