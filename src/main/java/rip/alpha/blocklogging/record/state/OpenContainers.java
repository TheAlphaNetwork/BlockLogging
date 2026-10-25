package rip.alpha.blocklogging.record.state;

import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.record.model.ContainerSession;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class OpenContainers {

    private final Map<UUID, ContainerSession> sessions = new HashMap<>();

    public void open(UUID player, ContainerSession session) {
        this.sessions.put(player, session);
    }

    @Nullable
    public ContainerSession close(UUID player) {
        return this.sessions.remove(player);
    }
}
