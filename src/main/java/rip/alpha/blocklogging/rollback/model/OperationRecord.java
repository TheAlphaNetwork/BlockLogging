package rip.alpha.blocklogging.rollback.model;

import lombok.Builder;
import rip.alpha.blocklogging.rollback.type.OperationState;

@Builder
public record OperationRecord(Operation operation, String sender, OperationState state, long time) {
}
