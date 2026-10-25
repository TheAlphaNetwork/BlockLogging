package rip.alpha.blocklogging.rollback.model;

import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.BlockPosition;

public record BlockHistory(long id, BlockPosition position, int type, int data, int action, @Nullable byte[] meta) {
}
