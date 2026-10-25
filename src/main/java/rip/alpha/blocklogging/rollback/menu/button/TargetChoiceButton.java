package rip.alpha.blocklogging.rollback.menu.button;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.rollback.state.RollbackDraft;
import rip.alpha.libraries.item.ItemBuilder;
import rip.alpha.libraries.menu.Menu;
import rip.alpha.libraries.menu.button.Button;
import rip.alpha.libraries.menu.editor.MenuSounds;
import rip.alpha.libraries.menu.model.ClickContext;

@RequiredArgsConstructor
public final class TargetChoiceButton extends Button {

    private final Menu parent;

    private final RollbackDraft draft;

    @Nullable
    private final String name;

    @Override
    public ItemStack item(Player player) {

        if (this.name == null)
            return ItemBuilder.of(Material.NETHER_STAR).name(Chat.PRIMARY + "Everyone").build();

        return ItemBuilder.of(Material.SKULL_ITEM, 3).skullOwner(this.name).name(Chat.PRIMARY + this.name).build();
    }

    @Override
    public void click(Player player, ClickContext context) {
        MenuSounds.success(player);

        this.draft.setTarget(this.name);
        this.parent.open(player);
    }
}
