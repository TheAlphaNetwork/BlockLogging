package rip.alpha.blocklogging.rollback.message;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.common.text.DurationLabel;
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

        String title = operation.kind().getTitle();

        String logs = "%,d logs".formatted(plan.matched());

        Chat.info(sender, "%s%s %swould change %s %s(%s)".formatted(Chat.TEXT, title, Chat.MUTED, summary, Chat.DARK, logs));
    }

    public void preview(CommandSender sender, RollbackPlan plan) {
        int blocks = plan.blocks().size();

        Chat.info(sender, "Previewing %s%,d %sblock changes, only you can see them.".formatted(Chat.ACCENT, blocks, Chat.MUTED));

        RichMessage.of(Chat.PREFIX)
                .then(Chat.button(Chat.GOOD, "Apply")).run("/bl apply").hover(Chat.MUTED + "Apply this change for real")
                .then(" ")
                .then(Chat.button(Chat.BAD, "Cancel")).run("/bl cancel").hover(Chat.MUTED + "Discard the preview")
                .send(sender);
    }

    public void started(CommandSender sender, Operation operation) {

        if (!operation.query().has(QueryFlag.SILENT))
            Chat.info(sender, "%s%s %sstarted, searching the logs...".formatted(Chat.TEXT, operation.kind().getTitle(), Chat.MUTED));
    }

    public void finished(CommandSender sender, Operation operation, ApplyResult result) {
        String summary = ChangeSummary.of(result.blocks(), result.hangings(), result.containers(), result.items(), result.entities());
        String title = operation.kind().getTitle();
        String time = DurationLabel.of(result.millis());
        String text = "%s%s %sdone in %s%s %s· %s ".formatted(Chat.TEXT, title, Chat.MUTED, Chat.ACCENT, time, Chat.DARK, summary);

        RichMessage.of(Chat.PREFIX + text)
                .then(Chat.button(Chat.BAD, "Undo")).run("/bl undo").hover(Chat.MUTED + "Revert this " + title.toLowerCase(Locale.ROOT))
                .send(sender);

        if (operation.query().has(QueryFlag.VERBOSE))
            Chat.info(sender, "%,d changes were skipped because the world already matched.".formatted(result.skipped()));
    }
}
