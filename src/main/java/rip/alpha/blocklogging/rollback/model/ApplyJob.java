package rip.alpha.blocklogging.rollback.model;

import org.bukkit.command.CommandSender;
import rip.alpha.blocklogging.rollback.type.RollbackKind;

import java.util.function.Consumer;

public record ApplyJob(CommandSender sender, RollbackKind kind, boolean silent, long budgetMillis, Consumer<ApplyResult> done) {
}
