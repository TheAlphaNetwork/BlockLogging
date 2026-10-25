package rip.alpha.blocklogging.rollback.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.permission.Permissions;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.query.type.QueryFlag;
import rip.alpha.blocklogging.rollback.RollbackService;
import rip.alpha.blocklogging.rollback.menu.button.CloseButton;
import rip.alpha.blocklogging.rollback.menu.button.DraftSummary;
import rip.alpha.blocklogging.rollback.menu.button.LabelButton;
import rip.alpha.blocklogging.rollback.menu.button.OptionButton;
import rip.alpha.blocklogging.rollback.menu.button.RunButton;
import rip.alpha.blocklogging.rollback.menu.button.TargetButton;
import rip.alpha.blocklogging.rollback.state.RollbackDraft;
import rip.alpha.blocklogging.rollback.type.RollbackKind;
import rip.alpha.libraries.menu.Menu;
import rip.alpha.libraries.menu.button.BackButton;
import rip.alpha.libraries.menu.button.Button;
import rip.alpha.libraries.menu.editor.Lore;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final class RollbackButtons {

    private final Menu menu;

    @Nullable
    private final Menu parent;

    private final RollbackDraft draft;

    private final RollbackService rollbacks;

    private final RollbackOptions options;

    RollbackButtons(Menu menu, @Nullable Menu parent, RollbackDraft draft, RollbackService rollbacks) {
        this.menu = menu;
        this.parent = parent;
        this.draft = draft;
        this.rollbacks = rollbacks;
        this.options = new RollbackOptions(draft);
    }

    Map<Integer, Button> of(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new LabelButton(Material.BOOK, "Rollback Builder", DraftSummary.lines(this.draft)));
        buttons.put(9, label(Material.WATCH, "Time", "How far back to look."));
        buttons.put(18, label(Material.COMPASS, "Radius", "Area around you, or your WorldEdit selection."));
        buttons.put(27, label(Material.CHEST, "What", "Which kind of logs to undo."));
        buttons.put(36, label(Material.SKULL_ITEM, "Player", "Only undo one player's changes."));

        this.options.times(buttons);
        this.options.radii(buttons);
        this.options.scopes(buttons);

        buttons.put(38, everyone());
        buttons.put(39, new TargetButton(this.menu, this.draft));

        if (this.parent != null)
            buttons.put(45, new BackButton(this.parent));

        buttons.put(47, run(Material.EYE_OF_ENDER, Chat.ACCENT + "Preview", RollbackKind.ROLLBACK, Set.of(QueryFlag.PREVIEW), false));
        buttons.put(48, run(Material.PAPER, Chat.ACCENT + "Count", RollbackKind.ROLLBACK, Set.of(QueryFlag.COUNT), false));
        buttons.put(50, run(Material.TNT, Chat.BAD + "Roll back", RollbackKind.ROLLBACK, Set.of(), true));
        buttons.put(51, run(Material.EMERALD, Chat.GOOD + "Restore", RollbackKind.RESTORE, Set.of(), true));
        buttons.put(53, new CloseButton());

        return buttons;
    }

    private Button label(Material icon, String name, String hint) {
        return new LabelButton(icon, name, Lore.lines(Chat.MUTED + hint));
    }

    private Button everyone() {
        return OptionButton.builder()
                .icon(Material.NETHER_STAR)
                .name("Everyone")
                .selected(this.draft.getTarget() == null)
                .select(() -> this.draft.setTarget(null))
                .build();
    }

    private Button run(Material icon, String name, RollbackKind kind, Set<QueryFlag> flags, boolean confirm) {
        String permission = kind == RollbackKind.ROLLBACK ? Permissions.ROLLBACK : Permissions.RESTORE;

        return RunButton.builder()
                .icon(icon)
                .name(name)
                .draft(this.draft)
                .kind(kind)
                .flags(flags)
                .confirm(confirm)
                .permission(permission)
                .rollbacks(this.rollbacks)
                .build();
    }
}
