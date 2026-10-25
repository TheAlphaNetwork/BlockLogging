package rip.alpha.blocklogging.record.model;

import rip.alpha.blocklogging.record.type.SessionAction;

public record SessionRecord(LogBase base, SessionAction action) implements LogRecord {
}
