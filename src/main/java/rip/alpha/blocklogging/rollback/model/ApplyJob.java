package rip.alpha.blocklogging.rollback.model;

import lombok.Builder;
import org.bukkit.command.CommandSender;
import rip.alpha.blocklogging.record.model.PendingWrite;
import rip.alpha.blocklogging.rollback.type.RollbackKind;

import java.util.function.Consumer;

@Builder
public record ApplyJob(
        CommandSender sender,
        RollbackKind kind,
        boolean silent,
        long budgetMillis,
        Consumer<PendingWrite> sink,
        Consumer<ApplyResult> done
) {
}
