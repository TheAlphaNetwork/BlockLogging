package rip.alpha.blocklogging.rollback.menu.button;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.libraries.item.ItemBuilder;
import rip.alpha.libraries.menu.button.Button;
import rip.alpha.libraries.menu.editor.Lore;
import rip.alpha.libraries.menu.editor.MenuSounds;
import rip.alpha.libraries.menu.model.ClickContext;

@RequiredArgsConstructor
public final class DraftButton extends Button {

    private final Material icon;

    private final String name;

    private final String value;

    private final Runnable next;

    @Override
    public ItemStack item(Player player) {
        return ItemBuilder.of(this.icon)
                .name(Chat.PRIMARY + this.name)
                .lore(Lore.value("Current", this.value), "", Chat.ACCENT + "Click to change")
                .build();
    }

    @Override
    public void click(Player player, ClickContext context) {
        MenuSounds.neutral(player);
        this.next.run();
    }
}
