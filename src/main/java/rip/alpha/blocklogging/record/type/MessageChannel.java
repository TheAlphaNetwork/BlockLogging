package rip.alpha.blocklogging.record.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import rip.alpha.blocklogging.storage.type.LogTable;

@Getter
@RequiredArgsConstructor
public enum MessageChannel {

    CHAT(LogTable.CHAT),
    COMMAND(LogTable.COMMAND);

    private final LogTable table;
}
