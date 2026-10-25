package rip.alpha.blocklogging.rollback.state;

import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.rollback.model.Operation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LastOperations {

    private final Map<String, Operation> operations = new ConcurrentHashMap<>();

    public void remember(String owner, Operation operation) {
        this.operations.put(owner, operation);
    }

    @Nullable
    public Operation last(String owner) {
        return this.operations.get(owner);
    }
}
