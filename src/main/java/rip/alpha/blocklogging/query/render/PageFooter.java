package rip.alpha.blocklogging.query.render;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.query.model.LookupPage;
import rip.alpha.libraries.text.message.RichMessage;

@UtilityClass
public class PageFooter {

    private final String COMMAND = "/bl lookup ";

    public RichMessage of(LookupPage page) {
        RichMessage footer = RichMessage.of(Chat.MUTED + "Page " + Chat.TEXT + page.page() + Chat.MUTED + "/" + Chat.TEXT + page.pages());

        footer.then(Chat.MUTED + " (" + page.total() + " results)  ");

        if (page.page() > 1)
            footer.then(Chat.ACCENT + "« Prev ").run(COMMAND + (page.page() - 1)).hover(Chat.MUTED + "Previous page");

        if (page.page() < page.pages())
            footer.then(Chat.ACCENT + "Next »").run(COMMAND + (page.page() + 1)).hover(Chat.MUTED + "Next page");

        String label = Chat.DARK + "  [" + Chat.ACCENT + "GUI" + Chat.DARK + "]";

        footer.then(label).run("/bl gui").hover(Chat.MUTED + "Open these results in a menu");

        return footer;
    }
}
