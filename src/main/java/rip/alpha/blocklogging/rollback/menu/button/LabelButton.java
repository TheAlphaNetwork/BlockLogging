package rip.alpha.blocklogging.rollback.menu.button;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.libraries.item.ItemBuilder;
import rip.alpha.libraries.menu.button.Button;

import java.util.List;

@RequiredArgsConstructor
public final class LabelButton extends Button {

    private final Material icon;

    private final String name;

    private final List<String> lore;

    @Override
    public ItemStack item(Player player) {
        return ItemBuilder.of(this.icon).name(Chat.PRIMARY + ChatColor.BOLD + this.name).lore(this.lore).build();
    }
}
