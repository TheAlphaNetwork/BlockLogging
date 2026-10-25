package rip.alpha.blocklogging.query.render;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.common.text.AgeLabel;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.query.describe.RowDescribers;
import rip.alpha.blocklogging.query.model.LookupRow;
import rip.alpha.libraries.text.message.RichMessage;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class RowLines {

    private final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    public RichMessage line(LookupRow row, boolean showLocation, long now) {
        BlockPosition position = row.position();

        String teleport = "/bl tp %s %d %d %d".formatted(position.world(), position.x(), position.y(), position.z());
        String description = RowDescribers.of(row.table()).describe(row);
        String hover = Chat.MUTED + DATE.format(Instant.ofEpochSecond(row.time())) + "\n" + Chat.MUTED + position.describe();

        if (row.rolledBack())
            description = ChatColor.STRIKETHROUGH + ChatColor.stripColor(description);

        RichMessage message = RichMessage.of(Chat.MUTED + AgeLabel.of(now - row.time()) + " ago ")
                .hover(hover + "\n" + Chat.ACCENT + "Click to teleport")
                .run(teleport)
                .then(Chat.DARK + "- " + Chat.PRIMARY + row.user() + " ")
                .then(description);

        if (showLocation && !position.world().isEmpty())
            message.then(Chat.DARK + " (" + position.describe() + ")").run(teleport).hover(Chat.ACCENT + "Click to teleport");

        return message;
    }
}
