package rip.alpha.blocklogging.rollback.plan;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.record.type.ContainerAction;
import rip.alpha.blocklogging.rollback.model.ContainerChange;
import rip.alpha.blocklogging.rollback.model.ContainerHistory;
import rip.alpha.blocklogging.rollback.type.RollbackKind;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
class ContainerPlanner {

    List<ContainerChange> plan(List<ContainerHistory> history, RollbackKind kind) {
        List<ContainerChange> changes = new ArrayList<>(history.size());

        for (ContainerHistory row : history) {

            if (row.item() == null)
                continue;

            boolean added = row.action() == ContainerAction.ADD.getId();

            changes.add(ContainerChange.builder()
                    .position(row.position())
                    .item(row.item())
                    .amount(row.amount())
                    .add((kind == RollbackKind.ROLLBACK) != added)
                    .id(row.id())
                    .build());
        }

        return changes;
    }
}
