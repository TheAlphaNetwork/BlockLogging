package rip.alpha.blocklogging.common.text;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@UtilityClass
public class Chat {

    public final String PRIMARY = ChatColor.GOLD.toString();
    public final String ACCENT = ChatColor.YELLOW.toString();
    public final String TEXT = ChatColor.WHITE.toString();
    public final String MUTED = ChatColor.GRAY.toString();
    public final String DARK = ChatColor.DARK_GRAY.toString();
    public final String GOOD = ChatColor.GREEN.toString();
    public final String BAD = ChatColor.RED.toString();
    public final String PREFIX = PRIMARY + ChatColor.BOLD + "BL " + DARK + "\u00bb " + MUTED;

    public void info(CommandSender sender, String message) {
        sender.sendMessage(PREFIX + message);
    }

    public void error(CommandSender sender, String message) {
        sender.sendMessage(PREFIX + BAD + message);
    }

    public String button(String color, String label) {
        return DARK + "[" + color + label + DARK + "]";
    }

    public String header(String title) {
        return PREFIX + TEXT + title;
    }
}
