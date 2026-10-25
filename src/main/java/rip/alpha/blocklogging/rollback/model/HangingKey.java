package rip.alpha.blocklogging.rollback.model;

import rip.alpha.blocklogging.common.model.BlockPosition;

public record HangingKey(BlockPosition position, int type) {
}
