package rip.alpha.blocklogging.query.render;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.query.model.LookupPage;
import rip.alpha.blocklogging.query.model.LookupRow;
import rip.alpha.blocklogging.query.model.LookupView;
import rip.alpha.blocklogging.query.model.NameColors;

import java.util.concurrent.TimeUnit;

@UtilityClass
public class LookupRenderer {

    public void render(CommandSender sender, LookupPage page, LookupView view, NameColors colors) {

        if (page.total() == 0) {
            Chat.info(sender, "Nothing logged here.");
            return;
        }

        if (page.rows().isEmpty()) {
            Chat.error(sender, page.pages() == 1 ? "There is only 1 page." : "There are only %d pages.".formatted(page.pages()));
            return;
        }

        long now = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        sender.sendMessage(Chat.header(view.title()));

        for (LookupRow row : page.rows())
            RowLines.line(row, view.showLocation(), now, colors).send(sender);

        PageFooter.of(page).send(sender);
    }
}
