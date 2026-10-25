package rip.alpha.blocklogging.rollback.model;

import rip.alpha.blocklogging.common.model.BlockPosition;

import java.util.List;

public record PendingPreview(Operation operation, List<BlockPosition> positions) {
}
