package rip.alpha.blocklogging.rollback.menu.button;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.libraries.item.ItemBuilder;
import rip.alpha.libraries.menu.button.Button;
import rip.alpha.libraries.menu.model.ClickContext;

public final class CloseButton extends Button {

    @Override
    public ItemStack item(Player player) {
        return ItemBuilder.of(Material.BARRIER).name(Chat.BAD + "Close").build();
    }

    @Override
    public void click(Player player, ClickContext context) {
        player.closeInventory();
    }
}
