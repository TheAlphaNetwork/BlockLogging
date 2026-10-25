package rip.alpha.blocklogging.rollback.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.alpha.blocklogging.common.permission.Permissions;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.config.ConfigStore;
import rip.alpha.blocklogging.query.exception.QueryException;
import rip.alpha.blocklogging.query.model.QueryInput;
import rip.alpha.blocklogging.rollback.RollbackService;
import rip.alpha.blocklogging.rollback.model.Operation;
import rip.alpha.blocklogging.rollback.type.RollbackKind;
import rip.alpha.libraries.command.annotation.Command;
import rip.alpha.libraries.command.annotation.Param;
import rip.alpha.libraries.command.execute.CommandMessages;

import java.util.Locale;

@RequiredArgsConstructor
public final class RollbackCommand {

    private static final String FILTERS = "u:<user> t:<time> r:<radius> a:<action> i:<include> e:<exclude> #preview #count";

    private final RollbackService rollbacks;

    private final ConfigStore configs;

    @Command(
            names = {"bl rollback", "bl rb"},
            permission = Permissions.ROLLBACK,
            description = "Undo changes that match the filters"
    )
    public void rollback(CommandSender sender, @Param(name = "filters", defaultValue = " ", wildcard = true) QueryInput input) {
        run(sender, input, RollbackKind.ROLLBACK);
    }

    @Command(
            names = {"bl restore", "bl rs"},
            permission = Permissions.RESTORE,
            description = "Redo changes that were rolled back"
    )
    public void restore(CommandSender sender, @Param(name = "filters", defaultValue = " ", wildcard = true) QueryInput input) {
        run(sender, input, RollbackKind.RESTORE);
    }

    @Command(
            names = {"bl undo"},
            permission = Permissions.ROLLBACK,
            description = "Revert your last rollback or restore, or another player's with blocklogging.undo.others"
    )
    public void undo(CommandSender sender, @Param(name = "player", defaultValue = " ") String player) {
        String owner = player.isBlank() ? sender.getName() : player.trim();

        if (!owner.equalsIgnoreCase(sender.getName()) && !sender.hasPermission(Permissions.UNDO_OTHERS)) {
            sender.sendMessage(CommandMessages.NO_PERMISSION);
            return;
        }

        this.rollbacks.undo(sender, owner);
    }

    @Command(names = {"bl apply"}, permission = Permissions.ROLLBACK, playerOnly = true, hidden = true)
    public void apply(Player player) {
        this.rollbacks.previews().apply(player);
    }

    @Command(names = {"bl cancel"}, permission = Permissions.ROLLBACK, playerOnly = true, hidden = true)
    public void cancel(Player player) {
        this.rollbacks.previews().cancel(player);
    }

    private void run(CommandSender sender, QueryInput input, RollbackKind kind) {

        if (input.isEmpty()) {
            Chat.error(sender, "Usage: /bl %s %s".formatted(kind.name().toLowerCase(Locale.ROOT), FILTERS));
            return;
        }

        try {
            this.rollbacks.run(sender, Operation.of(RollbackQueries.parse(sender, input, this.configs.getConfig().getRollback()), kind));
        } catch (QueryException exception) {
            Chat.error(sender, exception.getMessage());
        }
    }
}
