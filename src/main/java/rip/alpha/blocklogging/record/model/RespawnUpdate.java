package rip.alpha.blocklogging.record.model;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record RespawnUpdate(String id, @Nullable UUID entity) implements PendingWrite {
}
