package rip.alpha.blocklogging.rollback.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.alpha.blocklogging.common.permission.Permissions;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.common.text.TimeLabels;
import rip.alpha.blocklogging.query.type.QueryFlag;
import rip.alpha.blocklogging.rollback.RollbackService;
import rip.alpha.blocklogging.rollback.menu.button.DraftButton;
import rip.alpha.blocklogging.rollback.menu.button.RunButton;
import rip.alpha.blocklogging.rollback.menu.button.TargetButton;
import rip.alpha.blocklogging.rollback.state.RollbackDraft;
import rip.alpha.blocklogging.rollback.type.RollbackKind;
import rip.alpha.blocklogging.rollback.type.RollbackScope;
import rip.alpha.libraries.menu.Menu;
import rip.alpha.libraries.menu.button.Button;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
final class RollbackButtons {

    private final Menu menu;

    private final RollbackDraft draft;

    private final RollbackService rollbacks;

    Map<Integer, Button> of(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        RollbackScope scope = RollbackScope.of(this.draft.getScope());

        buttons.put(10, new DraftButton(Material.WATCH, "Time", TimeLabels.of(this.draft.seconds()), this.draft::nextTime));
        buttons.put(12, new DraftButton(Material.COMPASS, "Radius", this.draft.blocks() + " blocks", this.draft::nextRadius));
        buttons.put(14, new DraftButton(scope.getIcon(), "What", scope.getLabel(), () -> this.draft.setScope(this.draft.getScope() + 1)));
        buttons.put(16, new TargetButton(this.menu, this.draft));

        buttons.put(28, run(Material.EYE_OF_ENDER, "Preview", RollbackKind.ROLLBACK, Set.of(QueryFlag.PREVIEW), false));
        buttons.put(30, run(Material.PAPER, "Count", RollbackKind.ROLLBACK, Set.of(QueryFlag.COUNT), false));
        buttons.put(32, run(Material.TNT, "Roll back", RollbackKind.ROLLBACK, Set.of(), true));
        buttons.put(34, run(Material.EMERALD, "Restore", RollbackKind.RESTORE, Set.of(), true));

        return buttons;
    }

    private Button run(Material icon, String name, RollbackKind kind, Set<QueryFlag> flags, boolean confirm) {
        String permission = kind == RollbackKind.ROLLBACK ? Permissions.ROLLBACK : Permissions.RESTORE;

        return RunButton.builder()
                .icon(icon)
                .name(Chat.PRIMARY + name)
                .draft(this.draft)
                .kind(kind)
                .flags(flags)
                .confirm(confirm)
                .permission(permission)
                .rollbacks(this.rollbacks)
                .build();
    }
}
