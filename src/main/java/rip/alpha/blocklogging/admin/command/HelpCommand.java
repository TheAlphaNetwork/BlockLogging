package rip.alpha.blocklogging.admin.command;

import org.bukkit.command.CommandSender;
import rip.alpha.blocklogging.admin.model.HelpEntry;
import rip.alpha.blocklogging.common.permission.Permissions;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.libraries.command.annotation.Command;
import rip.alpha.libraries.text.message.RichMessage;

import java.util.List;

public final class HelpCommand {

    private static final List<HelpEntry> ENTRIES = List.of(
            new HelpEntry("/bl menu", "Control panel: inspector, nearby changes, rollback builder", Permissions.LOOKUP),
            new HelpEntry("/bl inspect", "Toggle the inspector, then click blocks and containers", Permissions.INSPECT),
            new HelpEntry("/bl lookup ", "u:<user> t:<time> r:<radius> a:<action> i:<include> e:<exclude>", Permissions.LOOKUP),
            new HelpEntry("/bl near", "Recent changes around you", Permissions.LOOKUP),
            new HelpEntry("/bl gui", "Open your last lookup as a menu", Permissions.LOOKUP),
            new HelpEntry("/bl rollback ", "Undo changes, same filters as lookup, plus #preview #count", Permissions.ROLLBACK),
            new HelpEntry("/bl restore ", "Redo changes that were rolled back", Permissions.RESTORE),
            new HelpEntry("/bl undo", "Revert your last rollback or restore", Permissions.ROLLBACK),
            new HelpEntry("/bl purge ", "t:<time> [r:#world] [#optimize], delete old logs", Permissions.PURGE),
            new HelpEntry("/bl status", "Logger and database status", Permissions.STATUS),
            new HelpEntry("/bl consumer", "Pause or resume the database writer", Permissions.CONSUMER),
            new HelpEntry("/bl reload", "Reload the config", Permissions.RELOAD)
    );

    @Command(
            names = {"bl", "bl help", "blocklogging", "blocklogging help", "co", "co help"},
            description = "Show the BlockLogging commands"
    )
    public void help(CommandSender sender) {
        sender.sendMessage(Chat.header("Commands"));

        for (HelpEntry entry : ENTRIES) {

            if (sender.hasPermission(entry.permission()))
                line(entry).send(sender);
        }
    }

    private RichMessage line(HelpEntry entry) {
        return RichMessage.of(Chat.ACCENT + entry.command().trim())
                .suggest(entry.command())
                .hover(Chat.MUTED + "Click to type it")
                .then(Chat.DARK + " - " + Chat.MUTED + entry.description());
    }
}
