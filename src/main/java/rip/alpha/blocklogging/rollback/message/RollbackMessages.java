package rip.alpha.blocklogging.rollback.message;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.query.type.QueryFlag;
import rip.alpha.blocklogging.rollback.model.ApplyResult;
import rip.alpha.blocklogging.rollback.model.BlockTarget;
import rip.alpha.blocklogging.rollback.model.ContainerChange;
import rip.alpha.blocklogging.rollback.model.EntityRemoval;
import rip.alpha.blocklogging.rollback.model.EntityRespawn;
import rip.alpha.blocklogging.rollback.model.HangingChange;
import rip.alpha.blocklogging.rollback.model.ItemChange;
import rip.alpha.blocklogging.rollback.model.Operation;
import rip.alpha.blocklogging.rollback.model.RollbackPlan;
import rip.alpha.libraries.text.message.RichMessage;

import java.util.Locale;

@UtilityClass
public class RollbackMessages {

    public void count(CommandSender sender, Operation operation, RollbackPlan plan) {
        String summary = ChangeSummary.of(
                plan.count(BlockTarget.class),
                plan.count(HangingChange.class),
                plan.count(ContainerChange.class),
                plan.count(ItemChange.class),
                plan.count(EntityRespawn.class) + plan.count(EntityRemoval.class)
        );

        Chat.info(sender, "%s would touch %s %s(%d logs).".formatted(operation.kind().getTitle(), summary, Chat.MUTED, plan.matched()));
    }

    public void preview(CommandSender sender, RollbackPlan plan) {
        Chat.info(sender, "Previewing %s%d%s block changes, only you can see them.".formatted(Chat.TEXT, plan.blocks().size(), Chat.MUTED));

        RichMessage.of(Chat.PREFIX)
                .then(Chat.ACCENT + "[Apply]").run("/bl apply").hover(Chat.MUTED + "Apply this change for real")
                .then(" ")
                .then(Chat.ACCENT + "[Cancel]").run("/bl cancel").hover(Chat.MUTED + "Discard the preview")
                .send(sender);
    }

    public void started(CommandSender sender, Operation operation) {

        if (!operation.query().has(QueryFlag.SILENT))
            Chat.info(sender, "%s started, searching the logs...".formatted(operation.kind().getTitle()));
    }

    public void finished(CommandSender sender, Operation operation, ApplyResult result) {
        String summary = ChangeSummary.of(result.blocks(), result.hangings(), result.containers(), result.items(), result.entities());
        String time = "%d.%ds".formatted(result.millis() / 1000, result.millis() % 1000 / 100);

        Chat.info(sender, "%s finished in %s%s%s: %s.".formatted(operation.kind().getTitle(), Chat.TEXT, time, Chat.MUTED, summary));

        if (operation.query().has(QueryFlag.VERBOSE))
            Chat.info(sender, "%d changes were skipped because the world already matched.".formatted(result.skipped()));

        String hover = Chat.MUTED + "Revert this " + operation.kind().getTitle().toLowerCase(Locale.ROOT);

        RichMessage.of(Chat.PREFIX + "Made a mistake? ")
                .then(Chat.ACCENT + "[Undo]").run("/bl undo").hover(hover)
                .send(sender);
    }
}
