package rip.alpha.blocklogging.rollback.menu.button;

import lombok.Builder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.query.type.QueryFlag;
import rip.alpha.blocklogging.rollback.RollbackService;
import rip.alpha.blocklogging.rollback.model.Operation;
import rip.alpha.blocklogging.rollback.state.RollbackDraft;
import rip.alpha.blocklogging.rollback.type.RollbackKind;
import rip.alpha.libraries.item.ItemBuilder;
import rip.alpha.libraries.menu.button.Button;
import rip.alpha.libraries.menu.confirm.ConfirmationMenu;
import rip.alpha.libraries.menu.editor.MenuSounds;
import rip.alpha.libraries.menu.model.ClickContext;

import java.util.Set;

@Builder
public final class RunButton extends Button {

    private final Material icon;

    private final String name;

    private final RollbackDraft draft;

    private final RollbackKind kind;

    private final Set<QueryFlag> flags;

    private final boolean confirm;

    private final String permission;

    private final RollbackService rollbacks;

    @Override
    public ItemStack item(Player player) {
        return ItemBuilder.of(this.icon).name(this.name).lore(DraftSummary.lines(this.draft)).build();
    }

    @Override
    public void click(Player player, ClickContext context) {

        if (!player.hasPermission(this.permission)) {
            MenuSounds.fail(player);
            return;
        }

        if (!this.confirm) {
            run(player);
            return;
        }

        ConfirmationMenu.of(Chat.PRIMARY + this.kind.getTitle() + "?", confirmed -> {

            if (confirmed)
                run(player);
        }).info(item(player)).open(player);
    }

    private void run(Player player) {
        MenuSounds.success(player);
        player.closeInventory();

        Operation operation = Operation.of(DraftQueries.of(this.draft, player, this.flags), this.kind);

        this.rollbacks.run(player, operation);
    }
}
