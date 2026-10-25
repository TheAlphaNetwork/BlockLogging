package rip.alpha.blocklogging.rollback.menu.button;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.common.text.TimeLabels;
import rip.alpha.blocklogging.rollback.state.RollbackDraft;
import rip.alpha.blocklogging.rollback.type.RollbackScope;
import rip.alpha.libraries.menu.editor.Lore;

import java.util.List;

@UtilityClass
class DraftSummary {

    List<String> lines(RollbackDraft draft) {
        String target = draft.getTarget() == null ? "Everyone" : draft.getTarget();

        return Lore.lines(
                Lore.value("Time", TimeLabels.of(draft.seconds())),
                Lore.value("Radius", draft.blocks() + " blocks"),
                Lore.value("What", RollbackScope.of(draft.getScope()).getLabel()),
                Lore.value("Player", target)
        );
    }
}
