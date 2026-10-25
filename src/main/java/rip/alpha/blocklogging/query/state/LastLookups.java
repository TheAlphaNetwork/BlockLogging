package rip.alpha.blocklogging.query.state;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.query.model.SavedLookup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LastLookups {

    private final Map<String, SavedLookup> lookups = new ConcurrentHashMap<>();

    public void remember(CommandSender sender, SavedLookup lookup) {
        this.lookups.put(sender.getName(), lookup);
    }

    @Nullable
    public SavedLookup last(CommandSender sender) {
        return this.lookups.get(sender.getName());
    }

    public void forget(String name) {
        this.lookups.remove(name);
    }
}
