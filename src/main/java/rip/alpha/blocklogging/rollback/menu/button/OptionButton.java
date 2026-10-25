package rip.alpha.blocklogging.rollback.menu.button;

import lombok.Builder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.libraries.item.ItemBuilder;
import rip.alpha.libraries.menu.button.Button;
import rip.alpha.libraries.menu.editor.MenuSounds;
import rip.alpha.libraries.menu.model.ClickContext;

@Builder
public final class OptionButton extends Button {

    private final Material icon;

    private final int amount;

    private final String name;

    private final boolean selected;

    private final Runnable select;

    @Override
    public ItemStack item(Player player) {
        String state = this.selected ? Chat.GOOD + "▸ Selected" : Chat.MUTED + "Click to select";

        return ItemBuilder.of(this.icon)
                .amount(Math.max(1, this.amount))
                .name((this.selected ? Chat.GOOD : Chat.ACCENT) + this.name)
                .lore(state)
                .glow(this.selected)
                .build();
    }

    @Override
    public void click(Player player, ClickContext context) {

        if (this.selected || context.type() == ClickType.DOUBLE_CLICK)
            return;

        MenuSounds.neutral(player);
        this.select.run();
    }
}
