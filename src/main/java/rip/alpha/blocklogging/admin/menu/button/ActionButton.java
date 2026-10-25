package rip.alpha.blocklogging.admin.menu.button;

import lombok.Builder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.alpha.libraries.menu.button.Button;
import rip.alpha.libraries.menu.editor.MenuSounds;
import rip.alpha.libraries.menu.model.ClickContext;

import java.util.function.Consumer;
import java.util.function.Function;

@Builder
public final class ActionButton extends Button {

    private final Function<Player, ItemStack> icon;

    private final Consumer<Player> action;

    private final String permission;

    private final boolean close;

    @Override
    public ItemStack item(Player player) {
        return this.icon.apply(player);
    }

    @Override
    public void click(Player player, ClickContext context) {

        if (context.type() == ClickType.DOUBLE_CLICK)
            return;

        if (this.permission != null && !player.hasPermission(this.permission)) {
            MenuSounds.fail(player);
            return;
        }

        MenuSounds.neutral(player);

        if (this.close)
            player.closeInventory();

        this.action.accept(player);
    }
}
