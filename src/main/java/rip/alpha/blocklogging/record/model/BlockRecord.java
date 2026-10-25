package rip.alpha.blocklogging.record.model;

import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.record.type.BlockAction;

public record BlockRecord(LogBase base, int type, int data, BlockAction action, @Nullable BlockMeta meta) implements LogRecord {
}
