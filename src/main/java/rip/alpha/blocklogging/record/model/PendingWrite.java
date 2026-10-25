package rip.alpha.blocklogging.record.model;

public sealed interface PendingWrite permits LogRecord, RolledBackUpdate, RespawnUpdate, OperationWrite, PurgeRequest {
}
