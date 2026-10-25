package rip.alpha.blocklogging.record.model;

public sealed interface LogRecord extends PendingWrite
        permits BlockRecord, ContainerRecord, ItemRecord, SignRecord, KillRecord, MessageRecord, SessionRecord {

    LogBase base();
}
