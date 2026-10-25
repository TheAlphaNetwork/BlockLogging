package rip.alpha.blocklogging.rollback.model;

import lombok.Builder;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.BlockPosition;

@Builder
public record BlockHistory(String id, BlockPosition position, int type, int data, int action, @Nullable byte[] meta) {
}
