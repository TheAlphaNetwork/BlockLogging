package rip.alpha.blocklogging.record.model;

import rip.alpha.blocklogging.rollback.model.OperationRecord;

public record OperationWrite(OperationRecord record) implements PendingWrite {
}
