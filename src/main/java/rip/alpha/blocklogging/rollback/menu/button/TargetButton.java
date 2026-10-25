package rip.alpha.blocklogging.rollback.menu.button;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.rollback.menu.TargetMenu;
import rip.alpha.blocklogging.rollback.state.RollbackDraft;
import rip.alpha.libraries.item.ItemBuilder;
import rip.alpha.libraries.menu.Menu;
import rip.alpha.libraries.menu.button.Button;
import rip.alpha.libraries.menu.editor.MenuSounds;
import rip.alpha.libraries.menu.model.ClickContext;

@RequiredArgsConstructor
public final class TargetButton extends Button {

    private final Menu parent;

    private final RollbackDraft draft;

    @Override
    public ItemStack item(Player player) {
        String target = this.draft.getTarget();

        String name = target == null ? Chat.ACCENT + "Pick a player" : Chat.GOOD + target;

        ItemBuilder item = ItemBuilder.of(Material.SKULL_ITEM, 3).name(name).glow(target != null);

        if (target != null)
            item.skullOwner(target);

        return item.lore(Chat.MUTED + "Click to pick an online player").build();
    }

    @Override
    public void click(Player player, ClickContext context) {
        MenuSounds.neutral(player);
        new TargetMenu(this.parent, this.draft).open(player);
    }
}
