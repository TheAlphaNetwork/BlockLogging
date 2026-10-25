package rip.alpha.blocklogging.query.render;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.common.text.AgeLabel;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.query.describe.RowDescribers;
import rip.alpha.blocklogging.query.model.LookupRow;
import rip.alpha.blocklogging.query.model.NameColors;
import rip.alpha.libraries.text.message.RichMessage;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class RowLines {

    private final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MMM d, HH:mm:ss").withZone(ZoneId.systemDefault());

    public RichMessage line(LookupRow row, boolean showLocation, long now, NameColors colors) {
        BlockPosition position = row.position();

        String teleport = "/bl tp %s %d %d %d".formatted(position.world(), position.x(), position.y(), position.z());
        String description = RowDescribers.of(row.table()).describe(row);
        String age = AgeLabel.of(now - row.time());
        String date = DATE.format(Instant.ofEpochSecond(row.time()));
        String where = position.pretty() + " in " + position.world();
        String hover = "%s%s\n%s%s\n%sClick to teleport".formatted(Chat.TEXT, date, Chat.MUTED, where, Chat.ACCENT);

        if (row.rolledBack())
            description = Chat.DARK + ChatColor.STRIKETHROUGH + ChatColor.stripColor(description);

        RichMessage message = RichMessage.of(Chat.MUTED + " " + age + "  ")
                .hover(hover)
                .run(teleport)
                .then(colors.of(row) + row.user() + " ")
                .then(description);

        if (row.rolledBack())
            message.then(Chat.DARK + " (rolled back)");

        if (showLocation && !position.world().isEmpty())
            message.then(Chat.DARK + "  " + position.pretty()).run(teleport).hover(Chat.ACCENT + "Click to teleport");

        return message;
    }
}
