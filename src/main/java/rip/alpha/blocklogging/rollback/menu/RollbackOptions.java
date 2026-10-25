package rip.alpha.blocklogging.rollback.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import rip.alpha.blocklogging.common.text.TimeLabels;
import rip.alpha.blocklogging.rollback.menu.button.OptionButton;
import rip.alpha.blocklogging.rollback.state.RollbackDraft;
import rip.alpha.blocklogging.rollback.type.RollbackScope;
import rip.alpha.libraries.menu.button.Button;

import java.util.Map;

@RequiredArgsConstructor
final class RollbackOptions {

    private static final int TIME_ROW = 11;
    private static final int RADIUS_ROW = 20;
    private static final int SELECTION_SLOT = 26;
    private static final int SCOPE_ROW = 29;

    private final RollbackDraft draft;

    void times(Map<Integer, Button> buttons) {

        for (int index = 0; index < RollbackDraft.TIMES.length; index++) {
            int choice = index;

            buttons.put(TIME_ROW + index, OptionButton.builder()
                    .icon(Material.WATCH)
                    .amount(index + 1)
                    .name("Last " + TimeLabels.of(RollbackDraft.TIMES[index]))
                    .selected(this.draft.getTime() == index)
                    .select(() -> this.draft.setTime(choice))
                    .build());
        }
    }

    void radii(Map<Integer, Button> buttons) {

        for (int index = 0; index < RollbackDraft.RADII.length; index++) {
            int choice = index;

            buttons.put(RADIUS_ROW + index, OptionButton.builder()
                    .icon(Material.COMPASS)
                    .amount(index + 1)
                    .name(RollbackDraft.RADII[index] + " blocks")
                    .selected(!this.draft.isSelection() && this.draft.getRadius() == index)
                    .select(() -> this.draft.pickRadius(choice))
                    .build());
        }

        buttons.put(SELECTION_SLOT, OptionButton.builder()
                .icon(Material.WOOD_AXE)
                .name("WorldEdit selection")
                .selected(this.draft.isSelection())
                .select(() -> this.draft.setSelection(true))
                .build());
    }

    void scopes(Map<Integer, Button> buttons) {
        RollbackScope[] scopes = RollbackScope.values();

        for (int index = 0; index < scopes.length; index++) {
            int choice = index;

            buttons.put(SCOPE_ROW + index, OptionButton.builder()
                    .icon(scopes[index].getIcon())
                    .name(scopes[index].getLabel())
                    .selected(RollbackScope.of(this.draft.getScope()) == scopes[index])
                    .select(() -> this.draft.setScope(choice))
                    .build());
        }
    }
}
