package rip.alpha.blocklogging.rollback.message;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.query.type.QueryFlag;
import rip.alpha.blocklogging.rollback.model.ApplyResult;
import rip.alpha.blocklogging.rollback.model.Operation;
import rip.alpha.blocklogging.rollback.model.RollbackPlan;
import rip.alpha.libraries.text.message.RichMessage;

import java.util.Locale;

@UtilityClass
public class RollbackMessages {

    public void count(CommandSender sender, Operation operation, RollbackPlan plan) {
        Chat.info(sender, "%s would change %s%d%s blocks, %s%d%s container stacks and respawn %s%d%s entities (%d logs).".formatted(
                operation.kind().getTitle(),
                Chat.TEXT, plan.blocks().size(), Chat.MUTED,
                Chat.TEXT, plan.containers().size(), Chat.MUTED,
                Chat.TEXT, plan.entities().size(), Chat.MUTED,
                plan.matched()
        ));
    }

    public void preview(CommandSender sender, RollbackPlan plan) {
        Chat.info(sender, "Previewing %s%d%s block changes, only you can see them.".formatted(Chat.TEXT, plan.blocks().size(), Chat.MUTED));

        RichMessage.of(Chat.PREFIX)
                .then(Chat.ACCENT + "[Apply]").run("/bl apply").hover(Chat.MUTED + "Apply this change for real")
                .then(" ")
                .then(Chat.ACCENT + "[Cancel]").run("/bl cancel").hover(Chat.MUTED + "Discard the preview")
                .send(sender);
    }

    public void finished(CommandSender sender, Operation operation, ApplyResult result) {
        Chat.info(sender, "%s finished in %s%d.%ds%s: %s%d%s blocks, %s%d%s container stacks, %s%d%s entities.".formatted(
                operation.kind().getTitle(),
                Chat.TEXT, result.millis() / 1000, result.millis() % 1000 / 100, Chat.MUTED,
                Chat.TEXT, result.blocks(), Chat.MUTED,
                Chat.TEXT, result.containers(), Chat.MUTED,
                Chat.TEXT, result.entities(), Chat.MUTED
        ));

        if (operation.query().has(QueryFlag.VERBOSE))
            Chat.info(sender, "%d changes were skipped because the world already matched.".formatted(result.skipped()));

        String hover = Chat.MUTED + "Revert this " + operation.kind().getTitle().toLowerCase(Locale.ROOT);

        RichMessage.of(Chat.PREFIX + "Made a mistake? ")
                .then(Chat.ACCENT + "[Undo]").run("/bl undo").hover(hover)
                .send(sender);
    }
}
