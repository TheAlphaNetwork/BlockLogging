package rip.alpha.blocklogging.record.model;

import rip.alpha.blocklogging.record.type.MessageChannel;

public record MessageRecord(LogBase base, MessageChannel channel, String text) implements LogRecord {
}
