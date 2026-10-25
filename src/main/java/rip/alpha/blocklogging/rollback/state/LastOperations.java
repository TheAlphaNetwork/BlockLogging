package rip.alpha.blocklogging.rollback.state;

import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.rollback.model.Operation;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LastOperations {

    private final Map<String, Operation> operations = new ConcurrentHashMap<>();

    public void remember(String owner, Operation operation) {
        this.operations.put(key(owner), operation);
    }

    @Nullable
    public Operation last(String owner) {
        return this.operations.get(key(owner));
    }

    public void undone(Operation inverse) {
        this.operations.values().removeIf(operation -> undoes(inverse, operation));
    }

    private boolean undoes(Operation inverse, Operation operation) {
        return operation.query().equals(inverse.query()) && operation.kind() == inverse.kind().inverse();
    }

    private String key(String owner) {
        return owner.toLowerCase(Locale.ROOT);
    }
}
