package rip.alpha.blocklogging.query.menu.button;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.query.model.LookupPage;
import rip.alpha.libraries.item.ItemBuilder;
import rip.alpha.libraries.menu.button.Button;
import rip.alpha.libraries.menu.editor.Lore;

@RequiredArgsConstructor
public final class LookupInfoButton extends Button {

    private final LookupPage page;

    @Override
    public ItemStack item(Player player) {
        return ItemBuilder.of(Material.BOOK)
                .name(Chat.PRIMARY + "BlockLogging")
                .lore(
                        Lore.value("Results", String.valueOf(this.page.total())),
                        Lore.value("Page", this.page.page() + "/" + this.page.pages())
                )
                .build();
    }
}
