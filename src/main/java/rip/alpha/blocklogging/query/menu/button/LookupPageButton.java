package rip.alpha.blocklogging.query.menu.button;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.query.QueryService;
import rip.alpha.blocklogging.query.model.SavedLookup;
import rip.alpha.libraries.item.ItemBuilder;
import rip.alpha.libraries.menu.button.Button;
import rip.alpha.libraries.menu.editor.MenuSounds;
import rip.alpha.libraries.menu.model.ClickContext;

@RequiredArgsConstructor
public final class LookupPageButton extends Button {

    private final QueryService queries;

    private final SavedLookup lookup;

    private final int page;

    private final boolean next;

    @Override
    public ItemStack item(Player player) {
        return ItemBuilder.of(Material.ARROW)
                .name(Chat.ACCENT + (this.next ? "Next page \u00bb" : "\u00ab Previous page"))
                .lore(Chat.MUTED + "Go to page " + Chat.TEXT + this.page)
                .build();
    }

    @Override
    public void click(Player player, ClickContext context) {
        MenuSounds.neutral(player);
        this.queries.menu(player, this.lookup, this.page);
    }
}
