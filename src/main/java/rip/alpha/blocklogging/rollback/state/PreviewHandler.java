package rip.alpha.blocklogging.rollback.state;

import org.bukkit.entity.Player;
import rip.alpha.blocklogging.rollback.model.Operation;
import rip.alpha.blocklogging.rollback.model.RollbackPlan;

@FunctionalInterface
public interface PreviewHandler {

    void show(Player player, Operation operation, RollbackPlan plan);
}
