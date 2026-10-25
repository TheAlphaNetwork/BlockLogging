package rip.alpha.blocklogging.rollback.model;

import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.model.BlockMeta;

public record BlockTarget(BlockPosition position, int type, int data, @Nullable BlockMeta meta) implements Change {
}
