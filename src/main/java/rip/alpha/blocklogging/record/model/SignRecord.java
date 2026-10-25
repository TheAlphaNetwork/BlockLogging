package rip.alpha.blocklogging.record.model;

public record SignRecord(LogBase base, String text) implements LogRecord {
}
