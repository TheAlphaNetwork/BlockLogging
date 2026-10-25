package rip.alpha.blocklogging.query.render;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.query.model.LookupPage;
import rip.alpha.libraries.text.message.RichMessage;

@UtilityClass
public class PageFooter {

    private final String COMMAND = "/bl lookup ";

    public RichMessage of(LookupPage page) {
        String results = page.total() == 1 ? "1 result" : "%,d results".formatted(page.total());
        String status = "%s Page %s%d%s of %s%d".formatted(Chat.MUTED, Chat.TEXT, page.page(), Chat.MUTED, Chat.TEXT, page.pages());

        RichMessage footer = RichMessage.of(status);

        footer.then(Chat.DARK + " · " + Chat.MUTED + results + "  ");

        if (page.page() > 1)
            footer.then(Chat.button(Chat.ACCENT, "« Back")).run(COMMAND + (page.page() - 1)).hover(Chat.MUTED + "Previous page").then(" ");

        if (page.page() < page.pages())
            footer.then(Chat.button(Chat.ACCENT, "Next »")).run(COMMAND + (page.page() + 1)).hover(Chat.MUTED + "Next page").then(" ");

        return footer.then(Chat.button(Chat.ACCENT, "Menu")).run("/bl gui").hover(Chat.MUTED + "Open these results in a menu");
    }
}
