package rip.alpha.blocklogging.rollback.model;

import rip.alpha.blocklogging.common.model.BlockPosition;

public record KillHistory(long id, BlockPosition position, int type) {
}
