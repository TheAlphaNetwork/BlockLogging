package rip.alpha.blocklogging.rollback.plan;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.record.type.ItemAction;
import rip.alpha.blocklogging.rollback.model.ItemChange;
import rip.alpha.blocklogging.rollback.model.ItemHistory;
import rip.alpha.blocklogging.rollback.type.RollbackKind;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
class ItemPlanner {

    List<ItemChange> plan(List<ItemHistory> history, RollbackKind kind) {
        List<ItemChange> changes = new ArrayList<>(history.size());

        for (ItemHistory row : history) {

            if (row.item() == null || row.player() == null)
                continue;

            boolean dropped = row.action() == ItemAction.DROP.getId();

            changes.add(ItemChange.builder()
                    .player(row.player())
                    .item(row.item())
                    .amount(row.amount())
                    .give((kind == RollbackKind.ROLLBACK) == dropped)
                    .id(row.id())
                    .build());
        }

        return changes;
    }
}
