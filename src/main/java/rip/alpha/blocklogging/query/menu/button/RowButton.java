package rip.alpha.blocklogging.query.menu.button;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.common.text.AgeLabel;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.query.QueryService;
import rip.alpha.blocklogging.query.describe.RowDescribers;
import rip.alpha.blocklogging.query.model.ExactRadius;
import rip.alpha.blocklogging.query.model.LookupRow;
import rip.alpha.blocklogging.query.model.LookupView;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.query.render.RowIcons;
import rip.alpha.blocklogging.query.type.Action;
import rip.alpha.libraries.item.ItemBuilder;
import rip.alpha.libraries.menu.button.Button;
import rip.alpha.libraries.menu.editor.Lore;
import rip.alpha.libraries.menu.editor.MenuSounds;
import rip.alpha.libraries.menu.model.ClickContext;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public final class RowButton extends Button {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private final QueryService queries;

    private final LookupRow row;

    private final long now;

    @Override
    public ItemStack item(Player player) {
        String description = RowDescribers.of(this.row.table()).describe(this.row);

        BlockPosition position = this.row.position();

        List<String> lore = Lore.lines(
                Lore.value("By", this.row.user()),
                Lore.value("When", AgeLabel.of(this.now - this.row.time()) + " ago"),
                Chat.DARK + DATE.format(Instant.ofEpochSecond(this.row.time())),
                Lore.value("Where", position.world().isEmpty() ? "-" : position.describe())
        );

        if (this.row.rolledBack())
            lore.add(ChatColor.RED + "Rolled back");

        lore.add("");
        lore.add(ChatColor.YELLOW + "Left-Click " + Chat.MUTED + "to teleport");
        lore.add(ChatColor.YELLOW + "Right-Click " + Chat.MUTED + "to inspect this spot");

        return ItemBuilder.of(RowIcons.of(this.row)).name(description).lore(lore).build();
    }

    @Override
    public void click(Player player, ClickContext context) {
        BlockPosition position = this.row.position();

        if (position.world().isEmpty()) {
            MenuSounds.fail(player);
            return;
        }

        MenuSounds.neutral(player);

        if (context.type() == ClickType.RIGHT || context.type() == ClickType.SHIFT_RIGHT) {
            Query query = Query.at(new ExactRadius(position), Set.of(Action.BLOCK, Action.CONTAINER, Action.CLICK, Action.SIGN));

            this.queries.menu(player, query, new LookupView("Inspect " + position.describe(), false));
            return;
        }

        player.closeInventory();
        player.performCommand("bl tp %s %d %d %d".formatted(position.world(), position.x(), position.y(), position.z()));
    }
}
