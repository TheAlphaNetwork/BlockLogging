package rip.alpha.blocklogging.rollback.state;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.rollback.model.Operation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LastOperations {

    private final Map<String, Operation> operations = new ConcurrentHashMap<>();

    public void remember(CommandSender sender, Operation operation) {
        this.operations.put(sender.getName(), operation);
    }

    @Nullable
    public Operation last(CommandSender sender) {
        return this.operations.get(sender.getName());
    }
}
