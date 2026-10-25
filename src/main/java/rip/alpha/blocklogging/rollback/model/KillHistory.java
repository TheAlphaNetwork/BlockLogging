package rip.alpha.blocklogging.rollback.model;

import lombok.Builder;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.BlockPosition;

import java.util.UUID;

@Builder
public record KillHistory(String id, BlockPosition position, int type, @Nullable byte[] meta, @Nullable UUID respawned) {
}
