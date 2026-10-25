package rip.alpha.blocklogging.record.model;

import lombok.Builder;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.record.type.BlockAction;

@Builder
public record BlockRecord(LogBase base, int type, int data, BlockAction action, @Nullable BlockMeta meta) implements LogRecord {
}
