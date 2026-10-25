package rip.alpha.blocklogging.query.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.query.QueryService;
import rip.alpha.blocklogging.query.menu.button.LookupInfoButton;
import rip.alpha.blocklogging.query.menu.button.LookupPageButton;
import rip.alpha.blocklogging.query.menu.button.RowButton;
import rip.alpha.blocklogging.query.model.LookupPage;
import rip.alpha.blocklogging.query.model.SavedLookup;
import rip.alpha.libraries.menu.Menu;
import rip.alpha.libraries.menu.button.Button;
import rip.alpha.libraries.menu.fill.FillTemplate;
import rip.alpha.libraries.menu.layout.MenuSize;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public final class LookupMenu extends Menu {

    public static final int ROWS_PER_PAGE = 45;

    private static final int PREVIOUS_SLOT = 0;
    private static final int INFO_SLOT = 4;
    private static final int NEXT_SLOT = 8;

    private final QueryService queries;

    private final SavedLookup lookup;

    private final LookupPage page;

    @Override
    public String title(Player player) {
        return Chat.PRIMARY + "%s (%d/%d)".formatted(this.lookup.view().title(), this.page.page(), this.page.pages());
    }

    @Override
    public int size() {
        return MenuSize.MAX;
    }

    @Override
    public Map<Integer, Button> buttons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        long now = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        buttons.put(INFO_SLOT, new LookupInfoButton(this.page));

        if (this.page.page() > 1)
            buttons.put(PREVIOUS_SLOT, new LookupPageButton(this.queries, this.lookup, this.page.page() - 1, false));

        if (this.page.page() < this.page.pages())
            buttons.put(NEXT_SLOT, new LookupPageButton(this.queries, this.lookup, this.page.page() + 1, true));

        for (int index = 0; index < this.page.rows().size(); index++)
            buttons.put(MenuSize.ROW + index, new RowButton(this.queries, this.page.rows().get(index), now));

        return buttons;
    }

    @Override
    public FillTemplate fill() {
        return FillTemplate.FILL;
    }

    @Override
    public int fillStart() {
        return 0;
    }

    @Override
    public boolean autoUpdate() {
        return false;
    }
}
