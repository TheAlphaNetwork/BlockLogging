package rip.alpha.blocklogging.common.text;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@UtilityClass
public class Chat {

    public final String PRIMARY = ChatColor.DARK_AQUA.toString();
    public final String ACCENT = ChatColor.AQUA.toString();
    public final String TEXT = ChatColor.WHITE.toString();
    public final String MUTED = ChatColor.GRAY.toString();
    public final String DARK = ChatColor.DARK_GRAY.toString();
    public final String PREFIX = PRIMARY + "BlockLogging " + DARK + "» " + MUTED;

    private final String LINE = DARK + ChatColor.STRIKETHROUGH + "-----" + ChatColor.RESET;

    public void info(CommandSender sender, String message) {
        sender.sendMessage(PREFIX + message);
    }

    public void error(CommandSender sender, String message) {
        sender.sendMessage(PREFIX + ChatColor.RED + message);
    }

    public String header(String title) {
        return "%s %sBlockLogging %s| %s%s %s".formatted(LINE, PRIMARY, DARK, TEXT, title, LINE);
    }
}
