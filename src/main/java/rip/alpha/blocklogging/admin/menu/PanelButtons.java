package rip.alpha.blocklogging.admin.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.admin.menu.button.ActionButton;
import rip.alpha.blocklogging.common.permission.Permissions;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.config.ConfigStore;
import rip.alpha.blocklogging.inspect.InspectService;
import rip.alpha.blocklogging.query.QueryService;
import rip.alpha.blocklogging.query.model.AreaRadius;
import rip.alpha.blocklogging.query.model.LookupView;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.query.type.Action;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.rollback.RollbackService;
import rip.alpha.blocklogging.rollback.menu.RollbackMenu;
import rip.alpha.libraries.item.ItemBuilder;
import rip.alpha.libraries.menu.button.Button;
import rip.alpha.libraries.menu.editor.Lore;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public final class PanelButtons {

    private final InspectService inspect;

    private final QueryService queries;

    private final RollbackService rollbacks;

    private final RecordService records;

    private final ConfigStore configs;

    public Map<Integer, Button> of(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(10, inspector());
        buttons.put(12, near());
        buttons.put(14, builder());
        buttons.put(16, undo());
        buttons.put(22, status());

        return buttons;
    }

    private Button inspector() {
        return ActionButton.builder()
                .icon(viewer -> {
                    boolean enabled = this.inspect.isInspecting(viewer);

                    return ItemBuilder.of(Material.BLAZE_ROD)
                            .name(Chat.PRIMARY + "Inspector " + (enabled ? Chat.ACCENT + "On" : Chat.MUTED + "Off"))
                            .lore(Lore.lines(Chat.MUTED + "Click blocks to see their history.", "", Chat.ACCENT + "Click to toggle"))
                            .glow(enabled)
                            .build();
                })
                .permission(Permissions.INSPECT)
                .action(viewer -> viewer.performCommand("bl inspect"))
                .build();
    }

    private Button builder() {
        return ActionButton.builder()
                .icon(_ -> icon(Material.TNT, "Rollback Builder", "Pick a time, radius and player,", "preview it, then roll back."))
                .permission(Permissions.ROLLBACK)
                .action(viewer -> new RollbackMenu(this.rollbacks, new PanelMenu(this)).open(viewer))
                .build();
    }

    private Button undo() {
        return ActionButton.builder()
                .icon(_ -> icon(Material.WATCH, "Undo", "Revert your last rollback", "or restore."))
                .permission(Permissions.ROLLBACK)
                .close(true)
                .action(this.rollbacks::undo)
                .build();
    }

    private Button status() {
        return ActionButton.builder()
                .icon(_ -> StatusIcon.of(this.records, this.rollbacks))
                .permission(Permissions.STATUS)
                .action(_ -> {
                })
                .build();
    }

    private Button near() {
        String range = "within " + nearRadius() + " blocks of you.";

        return ActionButton.builder()
                .icon(_ -> icon(Material.COMPASS, "Nearby Changes", "Everything that changed", range))
                .permission(Permissions.LOOKUP)
                .action(viewer -> {
                    Location location = viewer.getLocation();

                    AreaRadius area = AreaRadius.builder()
                            .world(location.getWorld().getName())
                            .x(location.getBlockX())
                            .z(location.getBlockZ())
                            .size(nearRadius())
                            .build();

                    this.queries.menu(viewer, Query.at(area, Action.LOOKUP_DEFAULTS), new LookupView("Nearby Changes", true));
                })
                .build();
    }

    private int nearRadius() {
        return this.configs.getConfig().getLookup().getNearRadius();
    }

    private ItemStack icon(Material material, String name, String... lines) {
        String[] lore = new String[lines.length];

        for (int index = 0; index < lines.length; index++)
            lore[index] = Chat.MUTED + lines[index];

        return ItemBuilder.of(material).name(Chat.PRIMARY + name).lore(lore).build();
    }
}
